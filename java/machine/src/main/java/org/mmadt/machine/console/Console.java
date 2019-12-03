/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.console;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.model.Obj;

import javax.script.ScriptEngineManager;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Console {

    static {
        System.err.close(); // this is only to get around the parboiled illegal access issues. TODO: write a wrapper to intercept
    }

    private static final String HEADER = "" +
            "                                _____ _______ \n" +
            "                           /\\  |  __ |__   __|\n" +
            " _ __ ___  _ __ ___ _____ /  \\ | |  | | | |   \n" +
            "| '_ ` _ \\| '_ ` _ |_____/ /\\ \\| |  | | | |   \n" +
            "| | | | | | | | | | |   / ____ \\ |__| | | |   \n" +
            "|_| |_| |_|_| |_| |_|  /_/    \\_\\____/  |_|   \n" +
            "                                 mm-adt.org  ";


    private static final String HISTORY = ".mmadt_history";
    private static final String RESULT = "==>";
    private static final String Q = ":q";
    private static final String L = ":l";

    public static void main(final String[] args) throws Exception {
        System.setErr(System.out);
        String engineName = "mmlang";
        final ScriptEngineManager manager = new ScriptEngineManager();
        final Terminal terminal = TerminalBuilder.builder().name("mm-ADT Console").build();
        final DefaultHistory history = new DefaultHistory();
        final DefaultParser parser = new DefaultParser();
        final LineReader reader = LineReaderBuilder.builder()
                .appName("mm-ADT Console")
                .terminal(terminal)
                .highlighter(new DefaultHighlighter())
                .variable(LineReader.HISTORY_FILE, HISTORY)
                //.variable(LineReader.HISTORY_IGNORE, List.of(Q)) TODO: don't want to have :q in the history
                .history(history)
                .parser(parser)
                .build();
        ///////////////////////////////////
        terminal.writer().println(HEADER);
        terminal.flush();
        while (true) {
            try {
                String line = reader.readLine(engineName + Tokens.RANGLE + Tokens.SPACE);
                while (line.trim().endsWith("/")) {
                    line = line.trim().substring(0, line.length() - 1) + reader.readLine(Tokens.repeater(engineName.length(), Tokens.PERIOD) + Tokens.RANGLE + Tokens.SPACE);
                }
                ///////////////////
                if (line.equals(Q))
                    break;
                else if (line.equals(L))
                    manager.getEngineFactories().forEach(factory -> terminal.writer().println(RESULT + factory.getEngineName()));
                else if (line.startsWith(L))
                    engineName = line.replace(L, "").trim();
                else {
                    try {
                        ((Iterator<Obj>) manager.getEngineByName(engineName).eval(line)).forEachRemaining(o -> terminal.writer().println(RESULT + o.toString()));
                    } catch (final Exception e) {
                        terminal.writer().println(e.getMessage());
                        if (null == e.getCause())
                            throw e;
                        throw e.getCause();
                    }
                }
            } catch (final UserInterruptException e) {
                break;
            } catch (final Throwable e) {
                terminal.writer().println(e.getMessage());
            }
            terminal.flush();
        }
    }
}
