package org.mmadt.language.obj.op.branch
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Bool, Inst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait RepeatOp[A <: Obj] {
  this: A =>

  def repeat(branch: A)(until: Obj): A = RepeatOp(branch, until).exec(this)
  def until(until: Obj)(branch: A): A = RepeatOp(branch, until).exec(this)
}

object RepeatOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branch: A, until: Obj): Inst[A, A] = new VInst[A, A](g = (Tokens.repeat, List(branch, until.asInstanceOf[A])), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val oldInst = Inst.oldInst(inst)
    val until: Obj = Inst.resolveArg(start, oldInst.arg1)
    //
    start match {
      case _: Strm[_] => start.via(start, oldInst)
      case _: Value[_] if until.isInstanceOf[Bool] =>
        def loop(y: Obj): Obj = {
          strm(y.toStrm.values.filter(_.alive).flatMap(x => {
            val temp: Obj = x ===> oldInst.arg0[Obj]
            val doloop: Bool = temp ===> oldInst.arg1[Bool]
            if (doloop.toStrm.values.last.g) loop(temp).toStrm.values else temp.toStrm.values // TODO: note strm unrolling
          }))
        }
        loop(start)
      case _: Value[_] =>
        val times = until.asInstanceOf[IntValue].g
        var repeatStart = start;
        var i = 0
        while (repeatStart.alive && i < times) {
          i = i + 1
          repeatStart = repeatStart ===> oldInst.arg0[Obj]
        }
        repeatStart
      case _: Type[_] => start.via(start, oldInst)
    }
  }
}