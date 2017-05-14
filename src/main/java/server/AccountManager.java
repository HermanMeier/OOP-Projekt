package server;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class AccountManager {
  private final File usersFile;
  private final Map<String, String> users = new HashMap<>();

  AccountManager(File users) throws Exception {
    this.usersFile = users;
    this.users.put("admin", PasswordHandler.getSaltedHash("xmltosql"));
    loadUsers();
  }

  private void loadUsers() throws IOException {
    //reades usernames and hashed passwords from users file
    try (BufferedReader fromFile = new BufferedReader(new InputStreamReader(new FileInputStream(usersFile)))) {
      String line = fromFile.readLine();
      while (line != null) {
        String[] credentials = line.split("%%");
        users.put(credentials[0], credentials[1]);
        line = fromFile.readLine();
      }
    }
  }

  void saveUsers() throws Exception {
    //Writes usernames and hashed password and salt to users file
    try (BufferedWriter toFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(usersFile)))) {
      for (Map.Entry<String, String> stringStringEntry : users.entrySet()) {
        toFile.write(stringStringEntry.getKey()+"%%"+stringStringEntry.getValue());
        toFile.newLine();
      }
    }
  }

  boolean signUp(String userName, String password) throws Exception {
    if (users.containsKey(userName))  {
      //username taken
      return false;
    }
    else  {
      users.put(userName, PasswordHandler.getSaltedHash(password));
      return true;
    }
  }

  boolean logIn(String userName, String password) throws Exception {
    if (!users.containsKey(userName)) {
      //wrong username
      return false;
    }
    else if(PasswordHandler.check(password, users.get(userName))) {
      System.out.println("Login as "+userName+" successful at "+ LocalDateTime.now());
      return true;
    }
    else  {
      //wrong password
      return false;
    }
  }
}
