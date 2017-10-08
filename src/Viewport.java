final class Viewport
{
   public int row;
   public int col;
   public int numRows;
   public int numCols;

   public Viewport(int numRows, int numCols)
   {
      this.numRows = numRows;
      this.numCols = numCols;
   }

   public void shift(int col1, int row1)
   {
      col = col1;
      row = row1;
   }


   public boolean contains( Point p)
   {
      return p.y >= row && p.y < row + numRows &&
              p.x >= col && p.x < col + numCols;
   }

    public Point viewportToWorld( int col1, int row1)
    {
        return new Point(col1 + col, row1 + row);
    }

    public Point worldToViewport( int col1, int row1)
    {
        return new Point(col1 - col, row1 - row);
    }

}
