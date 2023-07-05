/**
 * Class that finds if a string is in a binary tree.
 */
public class PathFromRoot {
    /**
     * check if a string is in a binary tree using recursion
     * @param root the root of binary tree
     * @param str the string we check
     * @return true if we found the string inside, false otherwise
     */
    public static boolean doesPathExist(BinNode<Character> root, String str) {
        // TODO: Add your code for part A1 here...

        /** if we got an empty string */
        if (root == null) {
            return str.length() == 0;
        }

        if (str.length() == 0) {
            return true;
        }

        if (root.getData().equals(str.charAt(0))) {
            return doesPathExist(root.getLeft(), str.substring(1)) ||
                    doesPathExist(root.getRight(), str.substring(1));
        }

        return false;
    }
}
