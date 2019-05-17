// PROJECT 2
//Collaborator 1: Darman Khan (Net ID: dkhan2)
//Collaborator 2: Bahawar Dhillon (Net ID: bdhillon)

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HuffmanSubmit implements Huffman {

	class Node {

		private String bitstring;
		private int frequency;
		private Node left;
		private Node right;
		private boolean isLeaf;

		public Node() {
			left = right = null;
		}

		public Node(String val, int freq, Node l, Node r) {
			left = l;
			right = r;
			bitstring = val;
			frequency = freq;
		}

		public Node(String val, int freq) {
			bitstring = val;
			frequency = freq;
		}

		public String getBitString() {
			return bitstring;
		}

		public void setBitString(String bitstring) {
			this.bitstring = bitstring;
		}

		public Node Left() {
			return left;
		}

		public void setLeft(Node left) {
			this.left = left;
		}

		public Node Right() {
			return right;
		}

		public void setRight(Node right) {
			this.right = right;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

		public boolean isLeaf() {
			if (left == null && right == null) {
				isLeaf = true;
			} else {
				isLeaf = false;
			}
			return isLeaf;
		}

	}

	class NodeComparator implements Comparator<Node> {

		public int compare(Node node1, Node node2) {
			if (node1.frequency > node2.frequency) {
				return 1;
			} else if (node1.frequency < node2.frequency) {
				return -1;
			}
			return 0;
		}
	}

	public static void main(String[] args) {
		Huffman huffman = new HuffmanSubmit();
		huffman.encode("alice30.txt", "alice30.enc", "alicefreq.txt");
		huffman.encode("ur.jpg", "ur.enc", "urfreq.txt");

		huffman.decode("ur.enc", "ur_dec.jpg", "urfreq.txt");
		huffman.decode("alice30.enc", "alice_dec.txt", "alicefreq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same.
		// On linux and mac, you can use `diff' command to check if they are the same.

	}

	public void encode(String inputFile, String outputFile, String freqFile) {
		try {
			HashMap<String, Integer> freqmap = makefreqmap(inputFile);
			makefreqfile(freqmap, freqFile);
			PriorityQueue<Node> makenodequeue = queuefromfile(freqFile);
			Node root = maketree(makenodequeue);
			fileencoder(root, inputFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void decode(String inputFile, String outputFile, String freqFile) {
		// stores data read from encoded file
		String datadec = "";
		int sum = 0;
		int c = 0;

		PriorityQueue<Node> makenodequeue = queuefromfile(freqFile);
		// constructs huffmantree to perform decoding
		Node root = maketree(makenodequeue);

		// newmap stores data from huffman tree
		HashMap<String, String> newmap = new HashMap<String, String>();
		encoderhelp(root, "", newmap);
		BinaryOut outfile = new BinaryOut(outputFile);
		BinaryIn infile = new BinaryIn(inputFile);
		
		sum = gettotal(freqFile);

		while (!infile.isEmpty()) {
			if(c==sum) {
				break;
			}
			String data = null;
			boolean dir = infile.readBoolean();
			if (dir == true) {
				datadec = datadec + "1";
			} else {
				datadec = datadec + "0";
			}

			// checks if datadec is stored in newmap and assigns the key to data
			for (String key : newmap.keySet()) {
				if (newmap.get(key).equals(datadec)) {
					data = key;
					c++;
				}
			}

			// converts data from binary string to character and writes in outfile
			if (data != null) {
				char value = (char) (Integer.parseInt(data, 2));
				outfile.write(value);
				// restarts storing from next character
				datadec = "";
			}
			outfile.flush();

		}
	}

	// method to perform encoding
	public void fileencoder(Node node, String inputFile, String outputFile) {
		BinaryIn binaryfile = new BinaryIn(inputFile);
		String result = null;
		BinaryOut binout = new BinaryOut(outputFile);

		// compressedmap stores original binary string: compressed binary string
		HashMap<String, String> compressmap = new HashMap<>();
		encoderhelp(node, "", compressmap);

		// stores binary data read from original file
		ArrayList<String> binarylist = new ArrayList<String>();
		while (!binaryfile.isEmpty()) {
			char mychar = binaryfile.readChar();
			String binarystring = (Integer.toBinaryString(mychar));
			binarylist.add(binarystring);
		}

		// reads corresponding compressed binary string for each value from hashmap and
		// writes in file
		for (String binary : binarylist) {
			result = compressmap.get(binary);
			for (char c : result.toCharArray()) {
				if (c == '1') {
					binout.write(true);

				} else if (c == '0') {
					binout.write(false);

				}
			}
		}

		binout.flush();
		compressmap.clear();

	}

	// traverses through huffman to read and store compressed binary strings into
	// hashmap
	public void encoderhelp(Node rt, String st, HashMap<String, String> compressmap) {

		if (rt.isLeaf()) {
			compressmap.put(rt.bitstring, st);

		}

		else if (!rt.isLeaf() && rt != null) {

			encoderhelp(rt.right, st + "1", compressmap);
			encoderhelp(rt.left, st + "0", compressmap);
		}

	}

	// makes a priority queue using the frequency file
	public PriorityQueue<Node> queuefromfile(String freqFile) {
		HashMap<String, Integer> freqmap = new HashMap<>();
		File freq = new File(freqFile);
		try {
			Scanner sc = new Scanner(freq);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] command = line.split(":");
				String binarychar = command[0];
				int frequency = Integer.parseInt(command[1]);
				freqmap.put(binarychar, frequency);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return makenodequeue(freqmap);
	}

	// takes hashmap of frequencies and makes a priority queue
	public PriorityQueue<Node> makenodequeue(HashMap<String, Integer> freqmap) {
		HuffmanSubmit huffmanclass = new HuffmanSubmit();
		PriorityQueue<Node> myqueue = new PriorityQueue<Node>(new NodeComparator());

		for (String key : freqmap.keySet()) {
			Node rt = huffmanclass.new Node(key, freqmap.get(key));
			myqueue.add(rt);
		}
		return myqueue;
	}

	// takes a priority queue and constructs huffman tree
	public Node maketree(PriorityQueue<Node> pqueue) {
		HuffmanSubmit huffmanclass = new HuffmanSubmit();
		while (pqueue.size() != 1) {

			Node rt = huffmanclass.new Node();
			Node n1 = pqueue.poll();
			Node n2 = pqueue.poll();
			rt.setLeft(n1);
			rt.setRight(n2);
			rt.setFrequency(n1.frequency + n2.frequency);
			pqueue.add(rt);
		}

		return pqueue.poll();
	}

	//makes a hashmap of frequencies
	public HashMap<String, Integer> makefreqmap(String inputFile) {
		HashMap<String, Integer> freqmap = new HashMap<>();
		BinaryIn binaryfile = new BinaryIn(inputFile);
		while (!binaryfile.isEmpty()) {
			char mychar = binaryfile.readChar();
			String binarystring = (Integer.toBinaryString(mychar));
			if (freqmap.containsKey(binarystring)) {
				freqmap.replace(binarystring, freqmap.get(binarystring) + 1);
			}

			else {
				freqmap.put(binarystring, 1);
			}
		}

		return freqmap;
	}

	//makes a frequency file from a frequency hashmap
	public void makefreqfile(HashMap<String, Integer> freqmap, String freqFile) throws IOException {
		File freqfile = new File(freqFile);
		freqfile.createNewFile();
		FileWriter mywriter = new FileWriter(freqfile);

		for (String key : freqmap.keySet()) {
			mywriter.write(key + ":" + freqmap.get(key));
			mywriter.write(System.getProperty("line.separator"));
		}

		mywriter.flush();
		mywriter.close();
	}
	
	//calculates the total number of characters in file 
	public int gettotal (String freqFile){
		int sum = 0;
		HashMap<String, Integer> freqmap = new HashMap<>();
		File freq = new File(freqFile);
		try {
			Scanner sc = new Scanner(freq);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] command = line.split(":");
				String binarychar = command[0];
				int frequency = Integer.parseInt(command[1]);
				freqmap.put(binarychar, frequency);
				sum = sum+frequency;
			}
		}

		catch (IOException e) {e.printStackTrace();}

		return sum;
	}



}