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

        BinNode<Integer> sentinel = new BinNode<>(null);  // Sentinel node

        ArrayDeque<BinNode<Integer>> queue = new ArrayDeque<>();
        queue.offer(root);
        queue.offer(sentinel);  // Add the sentinel node

        while (!queue.isEmpty()) {
            BinNode<Integer> node = queue.poll();

            if (node == sentinel) {
                if (!queue.isEmpty()) {
                    queue.offer(sentinel);  // Add the sentinel node for the next level
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