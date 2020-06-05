package org.mmadt.language.mmlang

import org.asciidoctor.Asciidoctor
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry

class mmlangExtensionRegistry extends ExtensionRegistry {
  override def register(asciidoctor: Asciidoctor): Unit = {
    asciidoctor.javaExtensionRegistry().treeprocessor(classOf[SourceBlockProcessor]);
  }
}
