package org.mmadt.storage.obj.value.strm

import org.mmadt.language.Tokens
import org.mmadt.language.obj.value.strm.ObjStrm
import org.mmadt.language.obj.{Obj, ViaTuple, base}

class VObjStrm(val name: String = Tokens.obj, val values: Seq[Obj], val via: ViaTuple = base) extends ObjStrm

