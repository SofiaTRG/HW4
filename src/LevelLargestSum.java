import java.util.ArrayDeque;

/** Class to find the level with the largest sum in a binary tree. */
public class LevelLargestSum {

    /**
     * Finds the level with the largest sum in a binary tree.
     * @param root The root node of the binary tree.
     * @return The level with the largest sum. Returns -1 if the tree is empty.
     */
    public static int getLevelWithLargestSum(BinNode<Integer> root) {
        // TODO: Add your code for part A2 here...
        if (root == null) {
            return -1;
        }

        int maxLevel = 0;
        int maxSum = root.getData();
        int currentLevel = 0;
        int currentSum = 0;

        BinNode<Integer> flag = new BinNode<>(null);

        ArrayDeque<BinNode<Integer>> queue = new ArrayDeque<>();
        queue.offer(root);
        queue.offer(flag);  // Add the flag node

        while (!queue.isEmpty()) {
            BinNode<Integer> node = queue.poll();

            if (node == flag) {
                if (!queue.isEmpty()) {
                    queue.offer(flag);  // Add the flag node for the next level
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
