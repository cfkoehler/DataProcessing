package com.DpServer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import com.DpServer.util.Version;
import emissary.command.VersionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(description = "Dump the DpServer version")
public class DpVersionCommand extends VersionCommand {
    static final Logger LOG = LoggerFactory.getLogger(DpVersionCommand.class);

    @Override
    public String getCommandName() {
        return "version";
    }

    @Override
    public void setupCommand() {
        // no op
    }

    @Override
    public void run(CommandLine cl) {
        LOG.info("DpServer version: {} ", new Version().toString());
    }

}
