import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MultiLevelSortExample {

    public static void main(String[] args) {
        List<Unit> unitList = createUnitList();

        // 按d1, d2, d3, ... 排序
        unitList.sort(Comparator
                        .comparingDouble(Unit::getD1)
                        .thenComparingDouble(Unit::getD2)
                        .thenComparingDouble(Unit::getD3)
                // ... for other fields ...
        );

        // 打印排序结果
        unitList.forEach(unit -> System.out.println(unit.getId() + "," + unit.getD1() + ", " + unit.getD2() + ", " + unit.getD3()));
    }

    private static List<Unit> createUnitList() {
        List<Unit> unitList = new ArrayList<>();
        unitList.add(new Unit("机组1", 3.0, 2.0, 1.0));
        unitList.add(new Unit("机组2", 1.0, 3.0, 2.0));
        unitList.add(new Unit("机组3", 1.0, 2.0, 3.0));
        unitList.add(new Unit("机组4", 2.0, 1.0, 3.0));
        unitList.add(new Unit("机组5", 3.0, 1.0, 2.0));
        // ... add other units ...
        return unitList;
    }

    private static class Unit {
        private String id;
        private double d1;
        private double d2;
        private double d3;
        // ... other double fields ...

        public Unit(String id, double d1, double d2, double d3 /*, ... other parameters ... */) {
            this.id = id;
            this.d1 = d1;
            this.d2 = d2;
            this.d3 = d3;
            // ... initialize other fields ...
        }

        public double getD1() {
            return d1;
        }

        public double getD2() {
            return d2;
        }

        public double getD3() {
            return d3;
        }

        public String getId() {
            return id;
        }

        // ... getters for other fields ...
    }

}

