package me.vicasong.cmd.base;

import picocli.CommandLine;

import java.util.List;

/**
 * Inject command list
 *
 * @author vicasong
 * @since 2022-09-27 11:22
 */
public interface CommandsAutowired {

	void setCommandList(List<CommandLine> commands);
}
