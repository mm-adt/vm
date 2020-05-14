package org.mmadt.language.obj.value.strm

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj

trait ObjStrm extends Strm[Obj] with Obj {
  override def g: Any = throw LanguageException.typeNoGround(this)
}
