package ie.ucd.cobweb.cobwebsocial;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestInsert {

	public static void main(String[] args) throws IOException {

		File f=new File(Constant.FN);
		Utility.post("40", "40", "test tweet", new FileInputStream(f));
	}
}
