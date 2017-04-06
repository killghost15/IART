package logic;

import java.util.LinkedList;
import java.util.Queue;

import util.Utility;

public class Board {
	private Piece[][] board = null;
	private boolean winWhite = false;
	private boolean winBlack = false;
	private boolean[][] visited = null;

	/**
	 * Construtor vazio.
	 * Apenas cria um board [n]x[n] sem pe�as.
	 * Hard-coded para criar [14]x[14]
	 */
	public Board(){
		int n = 14;
		int col;
		int row;
		board = new Piece[n][n];

		for(row = 0; row < board.length; row++)
			for(col = 0; col < board.length; col++)
				board[row][col] = new Piece(row, col);	
	}

	/**
	 * Construtor de um board quadrado.
	 * O board � preenchido pela fun��o auxiliar FillWithPieces().
	 * @param n tamanho do lado do tabuleiro
	 * @throws Exception se o board tiver uma dimens�o muito pequena ou muito grande
	 */
	public Board(int n) throws IndexOutOfBoundsException{
		if(n < 12)
			throw new IndexOutOfBoundsException("Board demasiado pequeno!");
		else if(n > 20)
			throw new IndexOutOfBoundsException("Board demasiado grande!");

		int row;
		int col;
		board = new Piece[n][n];

		for(row = 0; row < board.length; row++)
			for(col = 0; col < board.length; col++)
				board[row][col] = new Piece(row, col);

		fillWithPieces();
	}

	/**
	 * @return the board
	 */
	public Piece[][] getBoard() {
		return board;
	}

	/**
	 * Devolve o tamanho do board
	 * @return tamanho do lado do tabuleiro
	 */
	public int getSize() {
		return board.length;
	}	

	public Boolean getWinnerWhite(){
		return winWhite;
	}

	public Boolean getWinnerBlack(){
		return winBlack;
	}

	/**
	 * Preenche o tabuleiro com pe�as. 
	 * O board � preenchido da seguinte maneira:	
	 * Coloca a primeira pe�a.
	 * Nessa linha coloca a cada 5 espa�os uma nova pe�a, alternando o jogador, at� n�o ter 5 espa�os entre a pe�a e o fim do tabuleiro.
	 * Depois para a pr�xima linha coloca a primeira pe�a duas posi��es � direita da primeira pe�a, preenchendo antecipadamente as posi��es antes dessa pe�a. 
	 */
	private void fillWithPieces(){
		int col;
		int row;
		int line_start_position = 1; //posi��o da primeira pe�a da linha
		int player = 0;
		int col_position_checker = line_start_position;
		int aux_pc;
		int col_player_picker = player;
		int aux_pp = 0;

		for(row = 0; row < board.length; row++){
			col_player_picker = player;
			col_position_checker = line_start_position;	

			//preencher o inicio da linha
			aux_pc = col_position_checker - 5;	
			aux_pp = col_player_picker;			
			while(aux_pc >= 0){
				aux_pp ^= 1;
				board[row][aux_pc].setPlayer(aux_pp);
				aux_pc -= 5;	
			}

			//preenchimento normal
			for(col = 0; col < board.length; col++){	
				if(col == col_position_checker){
					board[row][col].setPlayer(col_player_picker);
					col_position_checker += 5;

					col_player_picker ^= 1;
				}
			}

			//preparar a pr�xima linha
			player ^= 1;
			line_start_position += 2;
			if(line_start_position > 13){
				line_start_position = 0;
				player ^= 1;
			}
		}
	}

	/**
	 * Desenha o tabuleiro com as pe�as na consola.
	 * Primeiro desenha a linha de caracteres que representam as poss�veis colunas.
	 * Depois para cada linha, imprime o caracter que a representa, dependendo se � maior que 9 ou n�o, e depois o conte�do da linha.
	 */
	public void display(){
		System.out.print("    ");
		Utility.printLineOfChar(board.length);
		System.out.println();	
		System.out.print("   ");	

		Utility.printDashedLine(board.length);

		for(int row = 0; row < board.length; row++){
			if(row < 9)
				System.out.print(row+1 +"  |");
			else
				System.out.print(row+1 +" |");

			for(int col = 0; col < board.length; col++)
				System.out.print(board[row][col].getSymbol() + " ");

			System.out.println("|");
		}		
		System.out.print("   ");	

		Utility.printDashedLine(board.length);

		System.out.println();
	}

	/**
	 * Calcula quantas pe�as de ambos os jogadores est�o no tabuleiro.
	 * @return n�mero de pe�as no tabuleiro
	 */
	public int getNumPieces(){
		int numPieces = 0;
		for(int row = 0; row < board.length; row++)
			for(int col = 0; col < board.length; col++)
				if(board[row][col].getPlayer() != -1)
					numPieces++;
		return numPieces;
	}

	/**
	 * Verifica se a posi��o n�o tem nenhuma pe�a.
	 * @param row
	 * @param col
	 * @return true se a posi��o ainda n�o tiver pe�a, false se j� tiver
	 */
	public Boolean checkFreePosition(int row, int col){
		try{
			if(board[row][col].getPlayer() == -1){
				return true;
			}
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Bad coords in checkFreePosition().");
		}
		return false;
	}

	/**
	 * Compara o player dos argumentos com o player da pe�a.
	 * @param row
	 * @param col
	 * @param player
	 * @return true se forem o mesmo player, false se n�o forem ou se a posi��o n�o tiver pe�a
	 */
	public Boolean checkOwner(int row, int col, int player){
		//System.out.println("(" + (row+1) + "," + (col+1) + ") player: " + board[row][col].getPlayer());
		try{
			if(board[row][col].getPlayer() == player)
				return true;
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("Bad coords in checkOwner().");
		}
		return false;
	}

	/**
	 * Verifica se existem pe�as do player nas posi��es � volta de (row,col).
	 * @param Piece
	 * @return true se exister pelo menos um movimento poss�vel, false se n�o
	 */
	public Boolean checkHasMoves(Piece p){
		try{
			if((p.getRow() + 1) < board.length && checkOwner(p.getRow() + 1, p.getCol(), p.getPlayer()))
				return true;
			if((p.getRow() - 1) >= 0 && checkOwner(p.getRow() - 1, p.getCol(), p.getPlayer()))
				return true;
			if((p.getCol() + 1) < board.length && checkOwner(p.getRow(), p.getCol() + 1, p.getPlayer()))
				return true;
			if((p.getCol() - 1) >= 0 && checkOwner(p.getRow(), p.getCol() - 1, p.getPlayer()))
				return true;
		}
		catch(IndexOutOfBoundsException e){}

		return false;
	}

	/**
	 * Verifica se o movimento � v�lido.
	 * O movimento � v�lido quando a posi��o n�o est� j� ocupada e h� uma pe�a do jogador numa casa adjacente � desejada, quer na horizontal, quer na vertical.
	 * @param Piece Pe�a a ser verificada
	 * @return true se for v�lido, false se n�o for v�lido
	 */
	public Boolean checkValidMove(Piece p){
		if(!checkFreePosition(p.getRow(), p.getCol()))
			return false;

		if(checkHasMoves(p))
			return true;

		System.out.println("Not valid. There isn't a player " + p.getPlayer() + " piece next to (" + (p.getRow()+1) + "," + Utility.itoc(p.getCol()) + ").");
		return false;
	}

	/**
	 * Retorna a pe�a na posi��o (row,col).
	 * @param row linha desejada
	 * @param col coluna desejada
	 * @return Piece na posi��o
	 */
	public Piece getPiece(int row, int col){
		try{
			return board[row][col];
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("Bad coords in getPiece().");
		}
		return null;
	}

	/**
	 * Coloca uma pe�a do player na posi��o (row,col).
	 * N�o depende das regras do jogo, apenas tem que estar dentro do tabuleiro.
	 * @param Piece
	 * @return true se conseguiu rowocar, false se n�o conseguiu
	 */
	public Boolean setPiece(Piece p){
		try{
			if(checkValidMove(p)){
				//System.out.println("Peca colocada em (" + (p.getRow()+1) +"," + Utility.itoc(p.getCol()) + ")");
				board[p.getRow()][p.getCol()].setPlayer(p.getPlayer());
				return true;
			}
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("Bad coords in setPiece().");
		}
		return false;
	}

	/**
	 * Retira a pe�a do tabuleiro.
	 * Na pr�tica apenas atribui � pe�a o jogador -1 que representa a aus�ncia de jogador
	 * @param row
	 * @param col
	 */
	public void removePiece(int row, int col){
		try{
			board[row][col].resetPlayer();
			//System.out.println("Peca removida de (" + (row+1) +"," + Utility.itoc(col) + ")");
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("Bad coords in removePiece().");
		}
	}

	/**
	 * Coloca uma pe�a do player na posi��o (row,col).
	 * N�o depende das regras do jogo, apenas tem que ser uma posi��o livre.
	 * @param Piece
	 * @return true se conseguiu colocar, false se n�o conseguiu
	 */
	public Boolean setPieceAbs(Piece p){
		try{
			if(checkFreePosition(p.getRow(), p.getCol())){
				board[p.getRow()][p.getCol()].setPlayer(p.getPlayer());
				return true;
			}
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("Bad coords in setPieceAbs().");
		}
		return false;
	}

	/**
	 * Devolve todas as pe�as do player presentes no board.
	 * @param player
	 * @return Queue<Piece> Fila de todas as pe�as do player
	 */
	public Queue<Piece> getPlayerPieces(int player){ 
		Queue<Piece> player_pieces = new LinkedList<Piece>();

		for(Piece[] col: board)
			for(Piece p: col)
				if(p.getPlayer() == player)
					player_pieces.add(p);
		return player_pieces;

	}

	/**
	 * Verifica se o player Branco ganhou o jogo. 
	 * Procura a primeira pe�a Branca ao longo da linha 0 se n�o existir sabemos que � impossivel ter ganho, se encontrar usa o 
	 * {@link #auxwinnerWhite(int row, int col, boolean[][] a) auxwinnerWhite} para percorrer todas os locais � volta terminando com falso se n�o conseguir chegar ao outro extremo e mudando o estado do jogo para vit�ria se pelo contr�rio atingiu o outro extremo.
	 * @return true se fez a linha, false se n�o
	 */
	public Boolean winnerWhite() {
		visited = new boolean[board.length][board.length];
		winWhite = false;

		for(int i = 0; i < board.length; i++)
			if(board[i][0].getPlayer() == 0 && auxwinnerWhite(i, 0) && winWhite)
				return true;

		return false;
	}

	/**
	 * Neste caso para o white processa a posi��o atual [row][col] isto � termina com vit�ria se for o extremo certo associado ao jogador
	 * ,exemplo especifico,�ltima coluna do tabuleiro, s� tenta processar o local do tabuleiro se l� estiver uma pe�a do jogador e se ainda n�o tiver sido visitado.
	 * As posi��es j� visitadas s�o guardadas em visited.
	 * @param row linha que est� a tratar neste momento
	 * @param col coluna que est� a ser tratado neste momento
	 * @return se � promising ou n�o atrav�s de bool, true se continuar, false se n�o for util continuar por este caminho
	 */
	public Boolean auxwinnerWhite(int row, int col) {
		if(winWhite)
			return true;

		if(col == getSize()-1 && board[row][col].getPlayer() == 0){
			winWhite = true;
			return true;
		}

		if(board[row][col].getPlayer() == 0 && !visited[row][col]){
			visited[row][col]=true;

			if(row+1 < board.length && auxwinnerWhite(row+1,col))
				return true;
			else if(col+1 < board.length && auxwinnerWhite(row,col+1))
				return true;
			else if(row-1 >= 0  && auxwinnerWhite(row-1,col))
				return true;
			else if(col-1 >= 0 && auxwinnerWhite(row,col-1))
				return true;
		}

		return false;
	}

	/**
	 * Verifica se o player Preto ganhou o jogo.
	 * Procura a primeira pe�a Preta ao longo da linha 0, isto � o topo do tabuleiro se n�o existir sabemos que � impossivel ter ganho. 
	 * Se encontrar usa o {@link #auxwinnerBlack(int row, int col, boolean[][] a) auxwinnerBlack} para percorrer todos os locais � volta, 
	 * terminando com falso se n�o conseguir chegar ao outro extremo e mudando o estado do jogo para vit�ria se pelo contr�rio atingiu o outro extremo.
	 * @return true se fez a coluna, false se n�o
	 */
	public Boolean winnerBlack() {
		visited = new boolean[board.length][board.length];
		winBlack = false;

		for(int i = 0; i < board.length; i++)
			if(board[0][i].getPlayer() == 1 && auxwinnerBlack(0,i) && winBlack){
				return true;
			}

		return false;
	}

	/**
	 * Neste caso para o Black processa a posi��o atual [row][col] isto � termina com vit�ria se for o extremo certo associado ao jogador, 
	 * exemplo especifico, �ltima linha do tabuleiro, s� tenta processar o local do tabuleiro se l� estiver uma pe�a do jogador e se ainda n�o tiver sido visitado.
	 * As posi��es j� visitadas s�o guardadas em visited.
	 * @param row linha que est� a tratar neste momento
	 * @param col coluna que est� a ser tratado neste momento
	 * @return se � promising ou n�o atrav�s de bool, true se continuar, false se n�o for util continuar por este caminho
	 */
	public Boolean auxwinnerBlack(int row, int col) {
		if(winBlack)
			return true;

		if(row == getSize()-1 && board[row][col].getPlayer() == 1){
			winBlack = true;
			return true;
		}

		if(board[row][col].getPlayer() == 1 && !visited[row][col]){
			visited[row][col] = true;

			if(row+1 < board.length && auxwinnerBlack(row+1, col))
				return true;
			else if(col+1 < board.length && auxwinnerBlack(row, col+1))
				return true;
			else if(row-1 >= 0 && auxwinnerBlack(row-1, col))
				return true;
			else if(col-1 >= 0 && auxwinnerBlack(row, col-1))
				return true;
		}

		return false;
	}
}
