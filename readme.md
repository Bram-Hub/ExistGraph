# ExistGraph
## Authors
2005:
Nathaniel Timmons

## About
Source: ExistGraph.java - Main program; high level algorithm is here
	ExistNode.java  - Class definition; structure and functions are here

Goal: To create an automated theorem prover for alpha existential graphs.  Would use equivalence rules when possible, but would branch whenever the need would arise.  This goal has been met.

Some quirks about the program:
There is a bug with OrSplit (excluded middle) which causes an infinite loop. Could very well be because I am not storing what was split in the first place, like I did with the Split function, using the Assumptions Set.  Now, the program runs well enough without this; however, if there are any open branches, things like "(ab)" might show up.  These are easily deciphered to mean ~a | ~b, but, it would be better if the program could print out all assumptions for open branches, so that it could be useful for more than just validity checking.  There is also debug info; after each line, there is a slash (/), followed by nothing, or a string.  That is the assumptions string.

Working with the program:
For the longest time I did not even know what was posted online.  I had one document, but it seemed to be more on proving things about existential graphs than proving things in existential graphs.  Besides, the deduction system it gave was more bent towards a more traditional tree; I wanted to use the least amount of memory possible, so I wanted to base the next step off of the previous step alone.  Thus, the number of graphs I stored at one time was number of unresolved brances, which was always less than or equal to the number of forks plus one.  This document did also give me the idea to use the excluded middle rule to 'dress up' the open branch results.

Running the program:
Some notes:
A node is a cut, and everything in it.
A variable is a single literal not in a cut at all.

The program itself...

The deiteration function is the most time consuming.  It has to go through all the subgraphs on the sheet of assertion itself, and attempt to deiterate them.  I could possibly shorten the time taken by adding successful deiterations to the assumption list; however, since the deiteration function has problems as it stands, I cannot.  As of now, the deiteration needs multiple sweeps to find everything; most likely due to the fact that the data structure is changing as it is iterating through the vectors.  A solution could be to use a temporary clone of the graph, which does not get changed at all.  I did not want to do this unless no other choice is available, for it uses up more memory.  Looks like I may have to try it, though.

Deiteration has to go through each subgraph a number of times equal to the number of unique nodes and variables that are in the lower levels of the graph.  Which is why I would like to keep the usage of this method down.

Remove Tautology does what it says it does, removes all graphs in the form of (a()), where a is any graph.  A little thing this does is that it also removes double cuts; since that is a special case of (a())... a is nothing, though.  This just goes through every subgraph once, removing tautologies.

Double Cut Removal is also obvious.  This one works marvelously; for it gets everything in one pass, at least according to my tests.  It goes through every subgraph once, checking for double cuts, and removes them.  Starts high, so to get things in like this: (((()))).

Split takes a variable that is not in the assumptions list (or it's negation), and adds it to the graph, and the assumptions list.  A copy of the original graph has its negetion added to the graph and the assumptions list.  The function getNewChar checks if it is possible to make a new assumption.  It has to be somewhere in the graph, and it cannot be assumed already.  Split runs in constant time, but GetNewChar needs to form a textual representation of the graph first.  This requires going through each subgraph once.

OrSplit takes the first cut and splits it in two between two graphs.  Excluded middle, basically.  It grabs the first compatible graph, so at most, it will go through each subgraph of the sheet of assertion only, once.
