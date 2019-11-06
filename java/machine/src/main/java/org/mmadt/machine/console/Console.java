/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.console;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.util.MinimalProcessor;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Console {

    private static final String HEADER =
            "                                _____ _______ \n" +
                    "                           /\\  |  __ |__   __|\n" +
                    " _ __ ___  _ __ ___ _____ /  \\ | |  | | | |   \n" +
                    "| '_ ` _ \\| '_ ` _ |_____/ /\\ \\| |  | | | |   \n" +
                    "| | | | | | | | | | |   / ____ \\ |__| | | |   \n" +
                    "|_| |_| |_|_| |_| |_|  /_/    \\_\\____/  |_|   \n" +
                    "                                 mm-adt.org  ";


    private static final String PROMPT = "mmadt> ";
    private static final String RESULT = "==>";
    private static final String Q = ":q";
    private static final SimpleParser PARSER = Parboiled.createParser(SimpleParser.class);

    public static void main(final String[] args) throws Exception {
        final ParseRunner runner = new BasicParseRunner<>(PARSER.Source());
        final Terminal terminal = TerminalBuilder.terminal();
        terminal.echo(false);
        terminal.pause(true);
        final DefaultParser parser = new DefaultParser();
        final LineReader reader = LineReaderBuilder.builder()
                .appName("mm-ADT Console")
                .terminal(terminal)
                .parser(parser)
                .build();
        terminal.writer().println(HEADER);
        terminal.flush();
        String line;
        while (true) {
            try {
                line = reader.readLine(PROMPT);
                if (line.equals(Q)) break;
                final ParsingResult result = runner.run(line);
                if (!result.valueStack.isEmpty()) {
                    final Obj obj = (Obj) result.valueStack.pop();
                    new MinimalProcessor<>(obj instanceof Inst ? (Inst) obj : obj.access()).iterator(obj).forEachRemaining(o -> {
                        terminal.writer().println(RESULT + o.toString());
                    });
                    terminal.flush();
                }
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
