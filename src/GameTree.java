import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * A model for the game of 20 questions
 *
 * @author Rick Mercer
 */
public class GameTree
{
	private Node root;
	private Node current;
	private String fileName;

	private class Node {
		String data;
		Node left, right;

		public Node(String data) { this.data = data; }
	}

	private boolean isAnswer(Node node) { 
		String line = node.data;
		return !line.substring(line.length() - 1).equals("?"); 
	}

	private Node tree(Scanner scanner) {
		if (!scanner.hasNextLine()) return null;
		Node node = new Node(scanner.nextLine().trim());
		if (!isAnswer(node)) {
			node.left = tree(scanner);
			node.right = tree(scanner);
		}
		return node;
	}

	/**
	 * Constructor needed to create the game.
	 *
	 * @param fileName
	 *          this is the name of the file we need to import the game questions
	 *          and answers from.
	 */
	public GameTree(String fileName) {
		this.fileName = fileName;
		try {
			Scanner scanner = new Scanner(new File(fileName));
			root = current = tree(scanner);
		}
		catch (FileNotFoundException e) { System.out.println("File not found: " + fileName); }
	}

	/*
	 * Add a new question and answer to the currentNode. If the current node has
	 * the answer chicken, theGame.add("Does it swim?", "goose"); should change
	 * that node like this:
	 */
	// -----------Feathers?-----------------Feathers?------
	// -------------/----\------------------/-------\------
	// ------- chicken  horse-----Does it swim?-----horse--
	// -----------------------------/------\---------------
	// --------------------------goose--chicken-----------
	/**
	 * @param newQ
	 *          The question to add where the old answer was.
	 * @param newA
	 *          The new Yes answer for the new question.
	 */
	public void add(String newQ, String newA) {
		String oldAnswer = current.data;
		current.data = newQ;
		current.left = new Node(newA);
		current.right = new Node(oldAnswer);
	}

	/**
	 * True if getCurrent() returns an answer rather than a question.
	 *
	 * @return False if the current node is an internal node rather than an answer
	 *         at a leaf.
	 */
	public boolean foundAnswer() { return isAnswer(current); }

	/**
	 * Return the data for the current node, which could be a question or an
	 * answer.  Current will change based on the users progress through the game.
	 *
	 * @return The current question or answer.
	 */
	public String getCurrent() { return current.data; }

	/**
	 * Ask the game to update the current node by going left for Choice.yes or
	 * right for Choice.no Example code: theGame.playerSelected(Choice.Yes);
	 *
	 * @param yesOrNo
	 */
	public void playerSelected(Choice yesOrNo) {
		if (yesOrNo == Choice.Yes) current = current.left;
		else current = current.right;
	}

	/**
	 * Begin a game at the root of the tree. getCurrent should return the question
	 * at the root of this GameTree.
	 */
	public void reStart() { current = root; }

	private String toString(Node node, int literalNumber) { 
		if (node == null) return "";
		
		final int added = literalNumber + 1;
		String output = toString(node.right, added);
		
		for (int i = 0; i < literalNumber; i++) output += "- ";
		output += node.data + "\n";

		return output + toString(node.left, added);
	} 

	@Override
	public String toString() { return toString(root, 0); }

	private void saveGame(Node node, PrintWriter file) {
		if (node == null) return;

		file.println(node.data);
		saveGame(node.left, file);
		saveGame(node.right, file);
	}

	/**
	 * Overwrite the old file for this gameTree with the current state that may
	 * have new questions added since the game started.
	 *
	 */
	public void saveGame() {
		PrintWriter file = null;
		try { file = new PrintWriter(new File(fileName)); }
		catch (FileNotFoundException e) { System.out.println("File not found: " + fileName); }

		saveGame(root, file);
		file.close();
}}
