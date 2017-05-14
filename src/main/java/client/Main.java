package client;

import java.io.IOException;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) throws IOException {
    Client login = new Client(Arrays.asList("login", "signup", "exit", "?"));
    login.run(args);
  }
}
