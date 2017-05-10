package client.commands;

import java.io.IOException;

public interface Command {
  void beforeSend();

  void send() throws IOException;

  void afterSend() throws IOException;
}
