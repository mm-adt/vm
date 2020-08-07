package org.mmadt

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.{asType, zeroObj}
import org.scalatest.Matchers.assertResult

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TestUtil {

  private val engine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine

  def stringify(obj: Obj): String = if (obj.isInstanceOf[Strm[_]]) {
    if (!obj.alive)
      zeroObj.toString
    else
      obj.toStrm.values.foldLeft("{")((a, b) => a.concat(b + ",")).dropRight(1).concat("}")
  } else obj.toString

  def evaluate(start: Obj, middle: Obj, end: Obj, inst: Inst[Obj, Obj] = null, engine: mmADTScriptEngine = engine): Unit = {
    engine.eval(":")
    List(
      //engine.eval(s"${stringify(start)} => ${middle}"),
      start.compute(middle),
      start ==> middle,
      start `=>` middle,
     // start `=>` (start.range ==> middle),
     // start ==> (start.range ==> middle),
      start `=>` (middle.domain ==> middle),
      start ==> (middle.domain ==> middle),
      //start `=>` (asType(start.isolate) ==> middle),
      start ==> (asType(start.isolate) ==> middle),
      // (asType(start.isolate) ==> middle).trace.foldLeft(start)((a, b) => b._2.exec(a))
      // middle.trace.foldLeft(start)((a, b) => b._2.exec(a))
      // Inst.resolveToken(start,middle),
    ).foreach(example => {
      assertResult(end)(example)
    })
    if (null != inst)
      assertResult(end)(inst.exec(start))
  }
}

