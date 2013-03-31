import java.net.*;
import java.io.*;

public class SocketTest {
	public static void main(String[] args) throws Exception
	{
		Socket connection = new Socket("localhost", 8725);
		ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
		out.flush();
		ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
		String message = (String)in.readObject();
		System.out.println(message);
		message = (String)in.readObject();
		System.out.println(message);
		String response = "READY";
		out.writeObject(response);
		out.flush();
		message = (String)in.readObject();
		System.out.println(message);
	}
}