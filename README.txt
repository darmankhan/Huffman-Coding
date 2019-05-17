This program uses Huffman Coding methods to compress and decompress files and was a project in the CSC172 course at the University of Rochester.

Project 2
Collaborator 1: Darman Khan 
ID: 30873087
Email: dkhan2@u.rochester.edu

Collaborator 2: Bahawar Sharif Dhillon
ID: 30851139
Email: bdhillon@u.rocehster.edu

In this project the methods of the interface Huffman, encode and decode, are implemented. The encode method first creates a hashmap with all characters in a file and their frequencies. This is then copied into a file. Using this file we create nodes for each character and then put them into a priority queue (min heap) based on the frequency of the character node. This queue is then dequeued twice and these two nodes are joined to a parent node, the frequency of the parent is equal to the total frequency of the two children. This parent node is then put back and the process is repeated, this creates a binary tree. We traverse the binary tree and save the path of each node as a string. We create another hashmap that has the character representation and path (compressed code). We then read each character in the input file and write the compressed code for it into another file in bits.

For decoding we regenerate the tree from the frequency file and use that to make a hashmap. We read each bit and check if there is a corresponding character to it, if not we concatenate this current bit to the last one and repeat again. This allows us to decode compressed code, when a corresponding value is found it is written into the output file
