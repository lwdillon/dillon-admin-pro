package com.dillon.lw.updater;

import com.dillon.lw.api.infra.ClientAutoUpdaterApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.infra.controller.admin.client.vo.ClientUpdateRespVO;
import com.dtflys.forest.Forest;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Swing 客户端自动更新：
 * 1) 启动时检查服务端版本
 * 2) 版本不同提示用户
 * 3) 下载新 jar（显示进度和速度）
 * 4) 退出当前进程后替换旧 jar 并自动重启
 */
@Slf4j
public final class ClientAutoUpdater {

    private ClientAutoUpdater() {
    }

    public static void checkAndUpdateIfNeeded() {
        if (!Boolean.parseBoolean(System.getProperty("app.update.enabled", "true"))) {
            return;
        }

        Path currentJar = resolveCurrentJarPath();
        if (currentJar == null) {
            log.info("未检测到 jar 运行环境，跳过自动更新。");
            return;
        }

        String localVersion = System.getProperty("app.version", "0.0.0");
        ClientUpdateRespVO updateMeta = fetchUpdateMeta();
        if (updateMeta == null || isBlank(updateMeta.getVersion())) {
            return;
        }

        if (!isRemoteNewer(localVersion, updateMeta.getVersion())) {
            log.info("当前版本已是最新: local={}, remote={}", localVersion, updateMeta.getVersion());
            return;
        }

        int confirm = askUserToUpdate(localVersion, updateMeta.getVersion(), updateMeta.getNotes());
        if (confirm != JOptionPane.YES_OPTION) {
            log.info("用户取消更新，继续启动旧版本。");
            return;
        }

        try {
            Path downloadedJar = downloadNewJar(currentJar);
            startReplaceAndRestartScript(currentJar, downloadedJar);
            System.exit(0);
        } catch (Exception ex) {
            log.error("自动更新失败", ex);
            showErrorDialog("自动更新失败：" + safeMessage(ex));
        }
    }

    private static ClientUpdateRespVO fetchUpdateMeta() {
        try {
            CommonResult<ClientUpdateRespVO> commonResult = Forest.client(ClientAutoUpdaterApi.class).latestSwing();
            return commonResult.getCheckedData();
        } catch (RuntimeException e) {
            log.warn("拉取 Swing 版本信息异常", e);
            return null;
        }
    }

    private static int askUserToUpdate(String localVersion, String remoteVersion, String notes) {
        StringBuilder message = new StringBuilder();
        message.append("检测到新版本：").append(remoteVersion).append('\n');
        message.append("当前版本：").append(localVersion).append('\n');
        if (!isBlank(notes)) {
            message.append('\n').append("更新说明：").append('\n').append(notes).append('\n');
        }
        message.append('\n').append("是否现在更新并重启客户端？");

        AtomicInteger result = new AtomicInteger(JOptionPane.NO_OPTION);
        invokeAndWaitSilently(() -> result.set(JOptionPane.showConfirmDialog(
                null,
                message.toString(),
                "客户端更新",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        )));
        return result.get();
    }

    private static Path downloadNewJar(Path currentJar) throws IOException {
        Path targetDir = currentJar.getParent();
        String targetName = currentJar.getFileName().toString();
        Path tempJar = targetDir.resolve(targetName + ".download");
        if (Files.exists(tempJar)) {
            Files.delete(tempJar);
        }

        AtomicReference<ProgressDialog> dialogRef = new AtomicReference<>();
        invokeAndWaitSilently(() -> {
            ProgressDialog dialog = new ProgressDialog();
            dialog.progressBar.setIndeterminate(true);
            dialog.progressBar.setString("下载中");
            dialog.setVisible(true);
            dialogRef.set(dialog);
        });

        try {
            File downloadedFile = Forest.client(ClientAutoUpdaterApi.class)
                    .downloadFile(targetDir.toString(), tempJar.getFileName().toString(), progress -> {
                        long downloadedBytes = progress.getCurrentBytes();
                        long totalBytes = progress.getTotalBytes();
                        invokeLaterSafe(() -> {
                            ProgressDialog dialog = dialogRef.get();
                            if (dialog != null) {
                                dialog.updateProgress(downloadedBytes, totalBytes, 0D);
                            }
                        });
                    });
            if (downloadedFile == null || !downloadedFile.exists()) {
                throw new IOException("下载失败，文件不存在");
            }

            Files.move(tempJar, tempJar.resolveSibling(tempJar.getFileName() + ".new"), StandardCopyOption.REPLACE_EXISTING);
            return tempJar.resolveSibling(tempJar.getFileName() + ".new");
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw e;
        } finally {
            invokeLaterSafe(() -> {
                ProgressDialog dialog = dialogRef.get();
                if (dialog != null) {
                    dialog.dispose();
                }
            });
        }
    }

    private static void startReplaceAndRestartScript(Path currentJar, Path newJar) throws IOException {
        boolean windows = isWindows();
        Path script = currentJar.getParent().resolve(windows ? "update-and-restart.bat" : "update-and-restart.sh");
        String javaBin = Paths.get(System.getProperty("java.home"), "bin", windows ? "javaw.exe" : "java").toString();

        String content = windows
                ? buildWindowsScript(javaBin, currentJar, newJar)
                : buildUnixScript(javaBin, currentJar, newJar);
        Files.write(script, content.getBytes(StandardCharsets.UTF_8));

        if (!windows) {
            script.toFile().setExecutable(true);
        }

        ProcessBuilder pb = windows
                ? new ProcessBuilder("cmd", "/c", script.toString())
                : new ProcessBuilder("sh", script.toString());
        pb.directory(currentJar.getParent().toFile());
        pb.start();
    }

    private static String buildWindowsScript(String javaBin, Path currentJar, Path newJar) {
        return "@echo off\r\n"
                + "setlocal\r\n"
                + "timeout /t 2 /nobreak >nul\r\n"
                + "move /Y \"" + newJar.getFileName().toString() + "\" \"" + currentJar.getFileName().toString() + "\" >nul\r\n"
                + "start \"\" \"" + javaBin + "\" -jar \"" + currentJar.getFileName().toString() + "\"\r\n"
                + "del \"%~f0\"\r\n";
    }

    private static String buildUnixScript(String javaBin, Path currentJar, Path newJar) {
        return "#!/bin/sh\n"
                + "sleep 2\n"
                + "mv -f \"" + newJar.getFileName().toString() + "\" \"" + currentJar.getFileName().toString() + "\"\n"
                + "nohup \"" + javaBin + "\" -jar \"" + currentJar.getFileName().toString() + "\" >/dev/null 2>&1 &\n"
                + "rm -- \"$0\"\n";
    }

    private static Path resolveCurrentJarPath() {
        Path fromCommand = resolveJarPathFromCommand();
        if (isValidJarPath(fromCommand)) {
            return fromCommand.toAbsolutePath().normalize();
        }

        try {
            URI uri = ClientAutoUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path fromCodeSource = resolvePathFromUri(uri);
            if (isValidJarPath(fromCodeSource)) {
                return fromCodeSource.toAbsolutePath().normalize();
            }
        } catch (Exception e) {
            log.warn("解析当前 jar 路径失败", e);
        }

        Path fromClassResource = resolveJarPathFromClassResource();
        if (isValidJarPath(fromClassResource)) {
            return fromClassResource.toAbsolutePath().normalize();
        }
        return null;
    }

    private static Path resolveJarPathFromCommand() {
        String command = System.getProperty("sun.java.command", "").trim();
        if (isBlank(command)) {
            return null;
        }

        String firstToken = extractFirstToken(command);
        if (isBlank(firstToken) || !firstToken.toLowerCase(Locale.ROOT).endsWith(".jar")) {
            return null;
        }
        return Paths.get(firstToken).toAbsolutePath().normalize();
    }

    private static String extractFirstToken(String command) {
        if (isBlank(command)) {
            return "";
        }
        String trimmed = command.trim();
        if (trimmed.startsWith("\"")) {
            int end = trimmed.indexOf('"', 1);
            return end > 1 ? trimmed.substring(1, end) : trimmed.substring(1);
        }
        int space = trimmed.indexOf(' ');
        return space > 0 ? trimmed.substring(0, space) : trimmed;
    }

    private static Path resolvePathFromUri(URI uri) {
        if (uri == null) {
            return null;
        }
        String raw = uri.toString();
        if (raw.startsWith("jar:")) {
            int separator = raw.indexOf("!/");
            if (separator > 4) {
                String fileUri = raw.substring(4, separator);
                return Paths.get(URI.create(fileUri));
            }
            return null;
        }
        if (raw.startsWith("file:")) {
            return Paths.get(uri);
        }
        return null;
    }

    private static Path resolveJarPathFromClassResource() {
        try {
            String location = ClientAutoUpdater.class.getResource("ClientAutoUpdater.class").toString();
            if (!location.startsWith("jar:")) {
                return null;
            }
            int separator = location.indexOf("!/");
            if (separator <= 4) {
                return null;
            }
            String fileUri = location.substring(4, separator);
            return Paths.get(URI.create(fileUri));
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValidJarPath(Path path) {
        return path != null && Files.isRegularFile(path) && path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar");
    }

    private static boolean isRemoteNewer(String localVersion, String remoteVersion) {
        int[] local = parseVersion(localVersion);
        int[] remote = parseVersion(remoteVersion);
        int max = Math.max(local.length, remote.length);
        for (int i = 0; i < max; i++) {
            int l = i < local.length ? local[i] : 0;
            int r = i < remote.length ? remote[i] : 0;
            if (r > l) {
                return true;
            }
            if (r < l) {
                return false;
            }
        }
        return false;
    }

    private static int[] parseVersion(String version) {
        if (isBlank(version)) {
            return new int[]{0};
        }
        String normalized = version.replaceAll("[^0-9.]", "");
        if (isBlank(normalized)) {
            return new int[]{0};
        }
        String[] parts = normalized.split("\\.");
        int[] numbers = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                numbers[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                numbers[i] = 0;
            }
        }
        return numbers;
    }

    private static void showErrorDialog(String message) {
        invokeAndWaitSilently(() -> JOptionPane.showMessageDialog(
                null,
                message,
                "更新失败",
                JOptionPane.ERROR_MESSAGE
        ));
    }

    private static void invokeAndWaitSilently(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (Exception e) {
            log.warn("执行 EDT 任务失败", e);
        }
    }

    private static void invokeLaterSafe(Runnable action) {
        SwingUtilities.invokeLater(() -> {
            try {
                action.run();
            } catch (Exception e) {
                log.warn("执行 UI 更新失败", e);
            }
        });
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String safeMessage(Throwable ex) {
        if (ex == null || isBlank(ex.getMessage())) {
            return "未知错误";
        }
        return ex.getMessage();
    }



    private static final class ProgressDialog extends JDialog {
        private final JProgressBar progressBar = new JProgressBar();
        private final JLabel progressLabel = new JLabel("下载准备中...");
        private final JLabel speedLabel = new JLabel("速度: 0 KB/s");

        private ProgressDialog() {
            setTitle("正在下载新版本");
            setModal(false);
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            setSize(420, 130);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(8, 8));

            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressBar.setStringPainted(true);

            JPanel panel = new JPanel(new GridLayout(2, 1, 0, 6));
            panel.add(progressLabel);
            panel.add(speedLabel);

            add(progressBar, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);
        }

        private void updateProgress(long downloaded, long total, double speedBytesPerSec) {
            if (total > 0) {
                int percent = (int) Math.min(100, downloaded * 100 / total);
                progressBar.setIndeterminate(false);
                progressBar.setValue(percent);
                progressBar.setString(percent + "%");
                progressLabel.setText("已下载 " + humanSize(downloaded) + " / " + humanSize(total));
            } else {
                progressBar.setIndeterminate(true);
                progressBar.setString("下载中");
                progressLabel.setText("已下载 " + humanSize(downloaded));
            }
            speedLabel.setText("速度: " + humanSpeed(speedBytesPerSec));
        }

        private String humanSpeed(double bytesPerSec) {
            if (bytesPerSec < 1024D) {
                return String.format(Locale.US, "%.0f B/s", bytesPerSec);
            }
            if (bytesPerSec < 1024D * 1024D) {
                return String.format(Locale.US, "%.1f KB/s", bytesPerSec / 1024D);
            }
            return String.format(Locale.US, "%.2f MB/s", bytesPerSec / 1024D / 1024D);
        }

        private String humanSize(long bytes) {
            if (bytes < 1024L) {
                return bytes + " B";
            }
            if (bytes < 1024L * 1024L) {
                return String.format(Locale.US, "%.1f KB", bytes / 1024D);
            }
            return String.format(Locale.US, "%.2f MB", bytes / 1024D / 1024D);
        }
    }
}
