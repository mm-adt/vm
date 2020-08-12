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
      obj.toStrm.values.foldLeft("[")((a, b) => a.concat(b + ",")).dropRight(1).concat("]")
  } else obj.toString

  def evaluate(start: Obj, middle: Obj, end: Obj, inst: Inst[Obj, Obj] = null, engine: mmADTScriptEngine = engine, compile: Boolean = true): Unit = {
    engine.eval(":")
    val evaluating = List[Obj => Obj](
      s => engine.eval(s"${stringify(s)} => ${middle}"),
      s => s.compute(middle),
      s => s ==> middle,
      s => s `=>` middle,
    )
    val compiling = List[Obj => Obj](
      s => (asType(s.rangeObj) ==> middle).trace.foldLeft(s)((a, b) => b._2.exec(a)),
      s => middle.trace.foldLeft(s)((a, b) => b._2.exec(a)),
      s => s `=>` (start.range ==> middle),
      s => s ==> (start.range ==> middle),
      s => s `=>` (middle.domain ==> middle),
      s => s ==> (middle.domain ==> middle),
      s => s `=>` (asType(start.rangeObj) ==> middle),
      s => s ==> (asType(start.rangeObj) ==> middle))
    val instructioning = List[Obj => Obj](s => inst.exec(s))
    (evaluating ++
      (if (compile) compiling else Nil) ++
      (if (null != inst) instructioning else Nil))
      .foreach(example => assertResult(end)(example(start)))
  }
}

