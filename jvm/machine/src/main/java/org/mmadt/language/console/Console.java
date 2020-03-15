/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.console;


import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.mmadt.language.LanguageFactory;
import org.mmadt.language.Tokens;
import org.mmadt.language.jsr223.mmADTScriptEngine;
import org.mmadt.language.model.Model;
import org.mmadt.language.obj.type.RecType;
import scala.collection.JavaConverters;

import javax.script.ScriptEngineManager;

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
    private static final String MODEL_OP = ":model";
    private static final String MODEL = "model";
    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();

    public static void main(final String[] args) throws Exception {
        String engineName = "mmlang";
        final mmADTScriptEngine engine = LanguageFactory.getLanguage("mmlang").getEngine().get();
        final Terminal terminal = TerminalBuilder.builder().name("mm-ADT Console").build();
        final DefaultHistory history = new DefaultHistory();
        final DefaultParser parser = new DefaultParser();
        final LineReader reader = LineReaderBuilder.builder()
                .appName("mm-ADT Console")
                .terminal(terminal)
                .highlighter(new DefaultHighlighter())
                .variable(LineReader.HISTORY_FILE, HISTORY)
                .history(history)
                .parser(parser)
                .build();
        ///////////////////////////////////
        terminal.writer().println(HEADER);
        terminal.flush();

        while (true) {
            try {
                String line = reader.readLine(engineName + "> ");
                while (line.trim().endsWith("/")) {
                    line = line.trim().substring(0, line.length() - 1) + reader.readLine(".".repeat(engineName.length()) + "> ");
                }
                ///////////////////
                if (line.equals(QUIT_OP))
                    break;
                else if (line.equals(LANG_OP))
                    MANAGER.getEngineFactories().forEach(factory -> terminal.writer().println(RESULT + factory.getEngineName()));
                else if (line.startsWith(LANG_OP))
                    engineName = line.replace(LANG_OP, "").trim();
                else if (line.equals(MODEL_OP))
                    terminal.writer().println(Model.apply((RecType) engine.eval(Tokens.model())));
                else if (line.startsWith(MODEL_OP))
                    engine.put(MODEL, Model.apply((RecType) engine.eval(Tokens.model())).put(Model.apply((RecType) engine.eval(line.substring(6)))));
                else
                    JavaConverters.asJavaIterator(engine.eval(line).toStrm().value()).forEachRemaining(o -> terminal.writer().println(RESULT + o.toString()));
            } catch (final UserInterruptException e) {
                break;
            } catch (final Throwable e) {
                terminal.writer().println(e);
            }
            terminal.flush();
        }
    }

    /*private static Model loadFiles(final Terminal terminal, final mmADTScriptEngine engine, final String location) {
        if (null == engine.get(MODEL))
            engine.put(MODEL, Model.simple());
        final Model model = (Model) engine.get(MODEL);
        File file = new File(location);
        if (file.isDirectory()) {
            return Stream.of(Objects.requireNonNull(file.listFiles())).
                    filter(File::isFile).
                    filter(x -> x.getName().endsWith(engine.getFactory().getExtensions().get(0))).
                    peek(x -> terminal.writer().println(RESULT + "Loaded " + x)).
                    map(x -> loadFiles(terminal, engine, x.getAbsolutePath())).
                    reduce(Model::put).orElse(model);
        } else {
            try {
                assert model != null;
                return model.put(Model.apply((RecType<Type<Obj>, Type<Obj>>) engine.eval(Files.lines(file.toPath()).map(x -> x.trim()).reduce((a, b) -> a + " " + b).orElse("")).next()));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);

            }
        }
    }*/
}


