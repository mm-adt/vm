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

package org.mmadt.language.console;


import org.jline.builtins.Completers;
import org.jline.builtins.Widgets;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.mmadt.VmException;
import org.mmadt.language.LanguageFactory;
import org.mmadt.language.Tokens;
import org.mmadt.language.jsr223.mmADTScriptEngine;
import org.mmadt.language.obj.Obj$;
import org.mmadt.language.obj.Rec;
import org.mmadt.language.obj.type.__;
import scala.collection.JavaConverters;

import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Console {

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
    private static final String QUIT_OP = ":q";
    private static final String LANG_OP = ":lang";
    private static final String MM_MODEL = "data/model/mm.mm";
    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
    private static final Highlighter HIGHLIGHTER = new DefaultHighlighter();
    /*private static final Nano.SyntaxHighlighter HIGHLIGHT = Nano.SyntaxHighlighter.build(
            new ConfigurationPath(Paths.get("bin"), Paths.get(System.getProperty("user.home"), ".mmadt")).getConfig("mmlang.nanorc"), "mmlang");*/

    public static void main(final String[] args) throws Exception {
        String engineName = "mmlang";
        mmADTScriptEngine engine = LanguageFactory.getLanguage("mmlang").getEngine().get();
        final Terminal terminal = TerminalBuilder.builder().name("mm-ADT Console").build();
        final DefaultHistory history = new DefaultHistory();
        final DefaultParser parser = new DefaultParser();
        parser.setEofOnUnclosedBracket(DefaultParser.Bracket.CURLY, DefaultParser.Bracket.ROUND, DefaultParser.Bracket.SQUARE);

        // THIS IS ONLY FOR mmlang. MOVING FORWARD, MAKE COMPLETERS PART OF THE LANGUAGE PROVIDER INTERFACE
        final Completers.TreeCompleter completer = new Completers.TreeCompleter(
                JavaConverters
                        .seqAsJavaList(Tokens.reservedOps())
                        .stream()
                        .map(op -> Tokens.LBRACKET() + op + Tokens.COMMA())
                        .map(Completers.TreeCompleter::node)
                        .collect(Collectors.toList()).toArray(new Completers.TreeCompleter.Node[Tokens.reservedOps().length()]));
        final LineReader reader = LineReaderBuilder.builder()
                .appName("mm-ADT Console")
                .terminal(terminal)
                .highlighter(HIGHLIGHTER)
                .variable(LineReader.HISTORY_FILE, HISTORY)
                .history(history)
                .parser(parser)
                .completer(completer)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, IntStream.range(0, engineName.length()).mapToObj(x -> ".").collect(Collectors.joining()) + "> ")
                .variable(LineReader.INDENTATION, 2)   // indentation size
                .option(LineReader.Option.INSERT_BRACKET, true)   // insert closing bracket automatically
                .build();
        Widgets.AutopairWidgets autopairWidgets = new Widgets.AutopairWidgets(reader);
        autopairWidgets.enable();
        ///////////////////////////////////
        terminal.writer().println(HEADER);
        terminal.flush();
        // initial model is mm
        engine.getContext().getBindings(ScriptContext.ENGINE_SCOPE).put(Tokens.COLON(), __.apply(Tokens.anon()).model((Rec) engine.eval(Files.lines(Paths.get(MM_MODEL)).reduce("", (a, b) -> a + b + "\n"))));
        while (true) {
            try {
                String line = reader.readLine(engineName + "> ");
                ///////////////////
                if (line.equals(QUIT_OP))
                    break;
                else if (line.equals(LANG_OP))
                    MANAGER.getEngineFactories().forEach(factory -> terminal.writer().println(RESULT + factory.getEngineName()));
                else if (line.startsWith(LANG_OP)) {
                    engineName = line.replace(LANG_OP, "").trim();
                    engine = (mmADTScriptEngine) MANAGER.getEngineByName(engineName);
                } else
                    JavaConverters.asJavaIterator(Obj$.MODULE$.iterator(engine.eval(line))).forEachRemaining(o -> writeHighlighter(o, reader, terminal));
            } catch (final UserInterruptException e) {
                break;
            } catch (final VmException e) {
                AttributedString HIGHLIGHT_ERROR = HIGHLIGHTER.highlight(reader, "language error").styleMatches(Pattern.compile("language error"), AttributedStyle.BOLD);
                HIGHLIGHT_ERROR.print(terminal);
                terminal.writer().println(": " + e.getMessage());
            } catch (final Throwable e) {
                terminal.writer().println(e);
            }
            terminal.flush();
        }
    }

    public static void writeHighlighter(final Object obj, final LineReader reader, final Terminal terminal) {
        AttributedString HIGHLIGHT_RESULT = HIGHLIGHTER.highlight(reader, RESULT).styleMatches(Pattern.compile(RESULT), AttributedStyle.BOLD);
        AttributedString HIGHLIGHT_RANGE = HIGHLIGHTER.highlight(reader, obj.toString()).styleMatches(Pattern.compile("<="), AttributedStyle.BOLD);
        HIGHLIGHT_RESULT.print(terminal);
        HIGHLIGHT_RANGE.println(terminal);
    }
}


