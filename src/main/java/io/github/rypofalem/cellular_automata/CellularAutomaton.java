package io.github.rypofalem.cellular_automata;


public class CellularAutomaton {
	private boolean[][][] state;
	int size;
	long rule;

	public CellularAutomaton(boolean[][][] state, long rule) throws IllegalArgumentException{
		if (state.length != state[0].length || state[0].length != state[0][0].length){
			String message = String.format(
					"State must be a cube. Dimensions: %d, %d, %d",
					state.length, state[0].length, state[0][0].length);
			throw new IllegalArgumentException(message);
		}
		this.state = state;
		this.rule = rule;
		this.size = state.length;
	}

	public boolean[][][] iterate() {
		boolean[][][] newState = new boolean[size][size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					int neighbors = 0;
					neighbors = addNeighbor(neighbors, x, y, z, 1, 0, 0);
					neighbors = addNeighbor(neighbors, x, y, z, -1, 0, 0);
					neighbors = addNeighbor(neighbors, x, y, z, 0, 1, 0);
					neighbors = addNeighbor(neighbors, x, y, z, 0, -1, 0);
					neighbors = addNeighbor(neighbors, x, y, z, 0, 0, 1);
					neighbors = addNeighbor(neighbors, x, y, z, 0, 0, -1);
					newState[x][y][z] = ((rule >> neighbors) % 2) == 1;
				}
			}
		}
		state = newState;
		return state.clone();
	}

	private int addNeighbor(int neighbors, int x, int y, int z, int xOff, int yOff, int zOff) {
		neighbors = neighbors << 1;
		neighbors += state
				[Math.floorMod(x + xOff, size)]
				[Math.floorMod(y + yOff, size)]
				[Math.floorMod(z + zOff, size)] ? 1 : 0;
		return neighbors;
	}
}