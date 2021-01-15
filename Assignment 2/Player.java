import java.util.*;

public class Player {
    private static int max_depth = 1;
    private static int first_player;
    private static int second_player;

    /**
     * Performs a move
     *
     * @param gameState the current state of the board
     * @param deadline  time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        // First_player will be X
        first_player = gameState.getNextPlayer();
        second_player = first_player == 1 ? 2 : 1;

        int value;
        int bestValue = Integer.MIN_VALUE;
        int bestIndex = -1;

        for (int i = 0; i < nextStates.size(); i++) {
            System.err.println(i);
            value = alphabeta(nextStates.get(i), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, second_player);
            if (value >= bestValue) {
                bestValue = value;
                bestIndex = i;
            }
        }
        // System.err.println("hej");
        return nextStates.elementAt(bestIndex);
    }

    // Evaluation function
    public int evalFunction(GameState state, int player) {
        // System.err.println("eval " + player);
        int current_player = player;
        int other_player = 3 - player;
        int current_player_score = 0;
        int other_player_score = 0;
        int current_player_streak = 0;
        int other_player_streak = 0;

        // Evaluate Rows
        for (int r = 0; r < 4; r++) {
            current_player_streak = 0;
            other_player_streak = 0;
            for (int c = 0; c < 4; c++) {
                if (state.at(r, c) == current_player) {
                    other_player_streak = 0;
                    current_player_streak++;
                } else if (state.at(r, c) == other_player) {
                    current_player_streak = 0;
                    other_player_streak++;
                }
            }
            current_player_score += current_player_streak == 0 ? 0 : Math.pow(10, current_player_streak - 1);
            other_player_score += other_player_streak == 0 ? 0 : Math.pow(10, other_player_streak - 1);
        }

        // Evaluate Columns
        for (int c = 0; c < 4; c++) {
            current_player_streak = 0;
            other_player_streak = 0;
            for (int r = 0; r < 4; r++) {
                if (state.at(r, c) == current_player) {
                    other_player_streak = 0;
                    current_player_streak++;
                } else if (state.at(r, c) == other_player) {
                    current_player_streak = 0;
                    other_player_streak++;
                }
            }
            current_player_score += current_player_streak == 0 ? 0 : Math.pow(10, current_player_streak - 1);
            other_player_score += other_player_streak == 0 ? 0 : Math.pow(10, other_player_streak - 1);
        }

        // Evaluate Diagonal 1 (top left -> bottom right)
        for (int x = 0; x < 4; x++) {
            if (state.at(x, x) == current_player) {
                other_player_streak = 0;
                current_player_streak++;
            } else if (state.at(x, x) == other_player) {
                current_player_streak = 0;
                other_player_streak++;
            }
        }
        current_player_score += current_player_streak == 0 ? 0 : Math.pow(10, current_player_streak - 1);
        other_player_score += other_player_streak == 0 ? 0 : Math.pow(10, other_player_streak - 1);
        current_player_streak = 0;
        other_player_streak = 0;

        // Evaluate Diagonal 2 (top right -> bottom left)
        int col = 3;
        for (int row = 0; row < 4; row++) {
            if (state.at(row, col) == current_player) {
                other_player_streak = 0;
                current_player_streak++;
            } else if (state.at(row, col) == other_player) {
                current_player_streak = 0;
                other_player_streak++;
            }
            col--;
        }
        current_player_score += current_player_streak == 0 ? 0 : Math.pow(10, current_player_streak - 1);
        other_player_score += other_player_streak == 0 ? 0 : Math.pow(10, other_player_streak - 1);
        current_player_streak = 0;
        other_player_streak = 0;

        // System.err.println("cps " + current_player_score);
        // System.err.println("ops " + other_player_score);
        return (current_player_score - other_player_score);
    }

    /*
     * Alpha-Beta Pruning: state: Current state of the game alpha: Current best
     * achieveable value of first_player beta: Current best achieveable value of
     * second_player player: The current player returns: The minimax value of the
     * state
     */
    public int alphabeta(GameState state, int depth, double alpha, double beta, int player) {
        // System.err.println("alphabeta " + depth);
        Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);

        int v = 0;

        if (depth == max_depth || nextStates.size() == 0) {
            return evalFunction(state, player);
        }

        else {

            if (player == first_player) {
                v = Integer.MIN_VALUE;
                for (int i = 0; i < nextStates.size(); i++) {
                    v = Math.max(v, alphabeta(nextStates.get(i), depth + 1, alpha, beta, second_player));
                    System.err.println("max");
                    alpha = Math.max(alpha, v);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

            else if (player == second_player) {
                v = Integer.MAX_VALUE;
                for (int i = 0; i < nextStates.size(); i++) {
                    v = Math.min(v, alphabeta(nextStates.get(i), depth + 1, alpha, beta, first_player));
                    System.err.println("min");
                    beta = Math.min(beta, v);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        return v;
    }
}
