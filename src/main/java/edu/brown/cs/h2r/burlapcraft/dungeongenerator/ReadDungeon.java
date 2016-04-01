package edu.brown.cs.h2r.burlapcraft.dungeongenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.brown.cs.h2r.burlapcraft.block.BlockBlueRock;
import edu.brown.cs.h2r.burlapcraft.block.BlockBurlapStone;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ReadDungeon extends FileDungeon {
	
	public ReadDungeon(String filename, Pose _pose) throws IOException {
		super(filename, _pose);
		read();
	}
	
	public void read() throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(DIRECTORY + filename));
		String cur = r.readLine();
		
		int y = 0;
		int z = 0;
		
		while (cur != null) {
			if (cur.equals("")) {
				// if we encounter a blank line, this is a new xz-plane.
				// Increment y and reset x and z.
				y++;
				z = 0;
				
				cur = r.readLine();
				continue;
			}
			// otherwise, it's the next line on the current xz-plane.
			String[] line = cur.split("\\s");
			
			int x = 0;
			
			for (int i = 0; i < line.length; i++) {
				String token = line[i];
				if (!token.isEmpty()) {
					int t = Integer.parseInt(token);
					if (t == -1) {
						setPlayerStartOffset(Pose.fromXyz(x, getHeight()-y, z));
					}
					map.set(Integer.parseInt(token), x, y, z);
					x++;
				}
			}
			
			z++;
			cur = r.readLine();
		}
		r.close();
	}
	
	@Override
	protected void generate(World world, int x, int y, int z) {
		// treat the given coordinate as the top left
		System.out.println("dungeon generated");
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getLength(); j++) {
				for (int k = 0; k < getWidth(); k++) {
					int id = map.get(k, i, j);
					Block b = Block.getBlockById(id);
					world.setBlock(x + k, y + getHeight() - i, z + j, b);
				}
			}
		}
	}
}
