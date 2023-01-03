package com.DpServer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.DpServer.util.Version;
import emissary.command.VersionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandDescription = "Dump the DpServer version")
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
    public void run(JCommander jc) {
        LOG.info("DpServer version: {} ", new Version().toString());
    }

}
