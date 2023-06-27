/**
 *
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
        if (str.length() == 0) {
            return true;
        }
        /** we reached the end og the tree and found no match */
        if (root == null) {
            return false;
        }

        if (str.length() > 2) {
            return root.getData().equals(str.charAt(0));
        }
        return doesPathExist(root.getRight(), str.substring(1)) ||
                doesPathExist(root.getLeft(), str.substring(1));
    }
}
