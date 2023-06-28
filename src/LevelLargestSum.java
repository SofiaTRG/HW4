public class LevelLargestSum {
    public static int getLevelWithLargestSum(BinNode<Integer> root) {
        // TODO: Add your code for part A2 here...
        if (root.getData() == null) {
            return -1;
        }

        if (root.getLeft() == null && root.getRight() == null) {
            return 0;
        }

        if ((root.getLeft().getData() + root.getRight().getData() > root.getData())) {
            return (getLevelWithLargestSum(root.getLeft()) + getLevelWithLargestSum(root.getRight()));
        }
        return 1;
    }
}
