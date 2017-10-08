import java.util.List;
import processing.core.PImage;

final class Background
{
   private String id;
   public List<PImage> images;
   public int imageIndex;

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
   }

   public void setBackgroundCell(WorldModel world, Point pos)

   {
      world.background[pos.y][pos.x] = this;
   }
}
