// 树表数据类
class NodeData {
    String name;
    boolean status;

    NodeData(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString() {
        return name;
    }
}

