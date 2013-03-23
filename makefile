all: main run

main: Fanorona.java Grid.java Piece.java State.java StateMachine.java
	javac Fanorona.java Grid.java Piece.java StateMachine.java State.java

run: Fanorona.class Grid.class Piece.class StateMachine.class State.class
		java Fanorona
