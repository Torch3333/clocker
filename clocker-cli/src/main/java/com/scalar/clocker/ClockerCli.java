package com.scalar.clocker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.clocker.command.CommandModule;
import com.scalar.clocker.command.ListTimestamp;
import com.scalar.clocker.command.Register;
import picocli.CommandLine;

@CommandLine.Command(name = "clocker", mixinStandardHelpOptions = true, version = "1.0")
public class ClockerCli {
  public static void main(String[] args) {
    String[] commandArgs = args.length != 0 ? args : new String[] {"-h"};
    Injector injector = Guice.createInjector(new CommandModule());
    CommandLine commandLine =
        new CommandLine(new ClockerCli())
            .addSubcommand(Register.COMMAND_NAME, injector.getInstance(Register.class))
            .addSubcommand(ListTimestamp.COMMAND_NAME, injector.getInstance(ListTimestamp.class));

    int exitCode = commandLine.execute(commandArgs);
    System.exit(exitCode);
  }
}
