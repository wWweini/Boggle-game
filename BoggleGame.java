import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class BoggleGame implements BoggleGameInterface{

    private StringBuilder currentSolution;
    private HashSet<String> stringSet;
    private char [][] theBoard;
    private DictInterface D;

    @Override
    public char[][] generateBoggleBoard(int size) {
        if(size <= 0){
            return null;
        }
        int stringLength = size*size;
        if(stringLength <= 0){
            return null;
        }
        String s = generateRandomString(stringLength);
        char[][] board = new char[size][size];
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                board[i][j] = s.charAt(i*size+j);
            }
        }
        return board;
    }

    @Override
    public int countWords(char[][] boggleBoard, DictInterface dictionary) {
        theBoard = boggleBoard;
        D = dictionary;
        stringSet = new HashSet<>();

        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[0].length; j++) {
                resetBoard();
                currentSolution = new StringBuilder();
				currentSolution.append(theBoard[i][j]);
                theBoard[i][j] = Character.toUpperCase(theBoard[i][j]);
                solve(i, j, 0,0);
            }
        }
        return stringSet.size();
    }

    @Override
    public int countWordsOfCertainLength(char[][] boggleBoard, DictInterface dictionary, int wordLength) {
        theBoard = boggleBoard;
        D = dictionary;
        stringSet = new HashSet<>();

        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[0].length; j++) {
                resetBoard();
                currentSolution = new StringBuilder();
				currentSolution.append(theBoard[i][j]);
                theBoard[i][j] = Character.toUpperCase(theBoard[i][j]);
                solve(i, j, 0, wordLength);
            }
        }
        return stringSet.size();
    }

    @Override
    public boolean isWordInDictionary(DictInterface dictionary, String word) {
        currentSolution = new StringBuilder();
        currentSolution.append(word);
        int res = dictionary.searchPrefix(currentSolution);
        if (res>0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isWordInBoard(char[][] boggleBoard, String word) {
        theBoard = boggleBoard;
        
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[0].length; j++) {
                resetBoard();
                ArrayList<Tile> tiles = new ArrayList<>();
                if (word.charAt(0) == theBoard[i][j]) {
                    return wordInBroad(word, i, j, 1,tiles);
                }
            }
        }
        return false;
    }

    @Override
    public String anyWord(char[][] boggleBoard, DictInterface dictionary) {
        theBoard = boggleBoard;
        D = dictionary;
        stringSet = new HashSet<>();

        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[0].length; j++) {
                resetBoard();
                currentSolution = new StringBuilder();
				currentSolution.append(theBoard[i][j]);
                theBoard[i][j] = Character.toUpperCase(theBoard[i][j]);
                solve(i, j, 0, 0);
            }
        }
        for(String s : stringSet){
            return s;
        }
        return null;
    }

    @Override
    public ArrayList<Tile> markWordInBoard(char[][] boggleBoard, String word) {
        theBoard = boggleBoard;
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[0].length; j++) {
                resetBoard();
                ArrayList<Tile> tiles = new ArrayList<>();
                if (word.charAt(0) == theBoard[i][j]) {
                    Tile firstTile = new Tile(i, j);
                    tiles.add(firstTile);
                    theBoard[i][j] = Character.toUpperCase(theBoard[i][j]);
                    if(wordInBroad(word, i, j, 1, tiles)){
                        return tiles;
                    }

                }
            }
        }
        return null;
    }

    @Override
    public boolean checkTiles(char[][] boggleBoard, ArrayList<Tile> tiles, String word) {
        theBoard = boggleBoard;
        currentSolution = new StringBuilder();
        for (Tile tile : tiles) {
            currentSolution.append(theBoard[tile.row][tile.col]);
        }
        if(currentSolution.toString().equalsIgnoreCase(word)){
            return true;
        }
        return false;
    }

    @Override
    public String anyWord(char[][] boggleBoard, DictInterface dictionary, int length) {
        theBoard = boggleBoard;
        D = dictionary;
        stringSet = new HashSet<>();
        
        for (int i = 0; i < theBoard.length; i++) {
            for (int j = 0; j < theBoard[0].length; j++) {
                resetBoard();
                currentSolution = new StringBuilder();
				currentSolution.append(theBoard[i][j]);
                theBoard[i][j] = Character.toUpperCase(theBoard[i][j]);
                solve(i, j, 0, length);
            }
        }
        for(String s : stringSet){
            if(s.length() == length){
                return s;
            }
        }
        return null;
    }

    private void solve(int row, int col, int depth, int wordLength){
		for(int direction=0; direction<8; direction++){
			if(isValid(row, col, direction)){
				currentSolution.append(nextChar(row, col, direction));
				//mark the letter as used
				Tile nextCoords = nextCoordinates(row, col, direction);
				theBoard[nextCoords.row][nextCoords.col] =
					Character.toUpperCase(theBoard[nextCoords.row][nextCoords.col]);
				int res = D.searchPrefix(currentSolution);

				if(res == 1){ //prefix but not word
					solve(nextCoords.row, nextCoords.col, depth + 1,wordLength);
				}
				if(res == 2 ){ //word but not prefix
                    if(wordLength==0 && currentSolution.length()>=3){
                        stringSet.add(currentSolution.toString());
					}
                    if (currentSolution.length() == wordLength){
                        stringSet.add(currentSolution.toString());
                    }
				}
                if(res == 3){ //word and prefix
                    if(wordLength==0 && currentSolution.length()>=3){
						stringSet.add(currentSolution.toString());
					}
                    if (currentSolution.length() == wordLength){
                        stringSet.add(currentSolution.toString());
                    }
                    solve(nextCoords.row, nextCoords.col, depth + 1, wordLength);
				}
				currentSolution.deleteCharAt(currentSolution.length()-1);
				theBoard[nextCoords.row][nextCoords.col] =
					Character.toLowerCase(theBoard[nextCoords.row][nextCoords.col]);       
			}
		}
	}

    private boolean wordInBroad(String word, int row, int col, int index, ArrayList<Tile> tiles) {
        if(index == word.length()) {
            return true;
        }
        for (int direction = 0; direction < 8; direction++) {
            if (isValid(row, col, direction)) {
                Tile nextCoords = nextCoordinates(row, col, direction);
                if (word.charAt(index)==theBoard[nextCoords.row][nextCoords.col]) {
                    //mark as used
                    theBoard[nextCoords.row][nextCoords.col] = Character.toUpperCase(theBoard[nextCoords.row][nextCoords.col]);    
                    //add to arraylist<tiles>
                    tiles.add(nextCoords);
                    if (wordInBroad(word, nextCoords.row, nextCoords.col, index + 1, tiles)) {
                        return true;
                    }
                    tiles.remove(tiles.size() - 1);
                    theBoard[nextCoords.row][nextCoords.col] = Character.toLowerCase(theBoard[nextCoords.row][nextCoords.col]);
                }
            }
        }
        return false;
    }
   
    private boolean isValid(int row, int col, int direction){
		Tile coords = nextCoordinates(row, col, direction);
		int c =coords.col;
		int r = coords.row;
		if((c>=theBoard[0].length)||(r>=theBoard.length)||(c==-1)||(r==-1)||(theBoard[r][c] == Character.toUpperCase(theBoard[r][c]))){
			return false;
		}
		return true;
	}

    private char nextChar(int row, int col, int direction){
		Tile coords = nextCoordinates(row, col, direction);
		return theBoard[coords.row][coords.col];
	}

    private void resetBoard(){
		for (int i = 0; i < theBoard.length; i++)
		{
			for (int j = 0; j < theBoard.length; j++)
			{
				theBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
			}
		}
	}

    private Tile nextCoordinates(int row, int col, int direction){
		Tile result = null;
		switch(direction){
			case 0: //up left
				result = new Tile(row-1, col-1);
				break;
			case 1: //up
				result = new Tile(row-1, col);
				break;
			case 2: //up right
				result = new Tile(row-1, col+1);
				break;
			case 3: //right
				result = new Tile(row, col+1);
				break;
			case 4: //bottom right
				result = new Tile(row+1, col+1);
				break;
			case 5: //bottom
				result = new Tile(row+1, col);
				break;
			case 6: //bottom left
				result = new Tile(row+1, col-1);
				break;
			case 7: //left
				result = new Tile(row, col-1);
				break;
		}
		return result;
    }

    private String generateRandomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString().toUpperCase();

        // System.out.println(generatedString);
        return generatedString;
    }
    
}

