package org.mmadt

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory.zeroObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TestUtil {
  def stringify(obj: Obj): String = if (obj.isInstanceOf[Strm[_]]) if (!obj.alive) zeroObj.toString else obj.toStrm.values.foldLeft("[")((a, b) => a.concat(b + ",")).dropRight(1).concat("]") else obj.toString
}

