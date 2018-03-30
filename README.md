This is a rewrite focussing on building a fuller parse tree.

Instead of parsing parts into (name, assignments) pairs, we want to parse 
parts into full chip in their own right.

Let's trying loading on demand. If you find a chip you don't have, then you 
load it recursively.

Need to make parser pre-parse all the defined chips.