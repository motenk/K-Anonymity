RUNNING THE MAIN PROGRAM
1. Open the src folder in terminal
2. Compile all java files using the command: 			 "javac */*.java"
3. Run the program with the command: 				     "java main/KAnon adult.csv 10 adult_output.csv adult_taxonomy_tree.txt"
4. To run your own dataset the command is in the format: "java main/KAnon <inputfile> <KValue> <outputfile> <taxonomytree>"

RUNNING THE ANALYSIS PROGRAM
1. Open the src folder in terminal
2. Compile all java files using the command: 			     "javac */*.java"
3a. Run the program using Adult dataset with the command:    "java main/Analyze adult.csv adult_taxonomy_tree.txt"
3a. Run the program using Baseball dataset with the command: "java main/Analyze baseball.csv baseball_taxonomy_tree.txt"
4. To run your own dataset the command is in the format:     "java main/Analyze <inputfile> <taxonomytree>"
