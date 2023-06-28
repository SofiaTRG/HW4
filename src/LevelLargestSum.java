import java.util.ArrayDeque;

public class LevelLargestSum {
    public static int getLevelWithLargestSum(BinNode<Integer> root) {
        // TODO: Add your code for part A2 here...
        if (root == null) {
            return -1;
        }

        int maxLevel = 0;
        int maxSum = root.getData();
        int currentLevel = 0;
        int currentSum = 0;

        ArrayDeque<BinNode<Integer>> queue = new ArrayDeque<>();
        queue.offer(root);
        queue.offer(null);

        while (!queue.isEmpty()) {
            BinNode<Integer> node = queue.poll();

            if (node == null) {
                if (!queue.isEmpty()) {
                    queue.offer(null);
                }

                if (currentSum > maxSum) {
                    maxSum = currentSum;
                    maxLevel = currentLevel;
                }

                currentSum = 0;
                currentLevel++;
            } else {
                currentSum += node.getData();

                if (node.getLeft() != null) {
                    queue.offer(node.getLeft());
                }

                if (node.getRight() != null) {
                    queue.offer(node.getRight());
                }
            }
        }

        return maxLevel;
    }
}