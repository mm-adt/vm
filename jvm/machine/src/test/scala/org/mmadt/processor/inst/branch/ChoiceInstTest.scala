package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class ChoiceInstTest extends FunSuite {

  test("[choose] w/ values") {
    assertResult(int(4))(
      int(0).plus(1).-<(
        int.is(int.gt(2)) --> int.mult(3) |
          int --> int.mult(4)) >-)

    assertResult(int(12))(
      int(0).plus(4).-<(
        int.is(int.gt(2)) --> int.mult(3) |
          int --> int.mult(4)) >-)

    assertResult(int(42))(
      int(0) ==> int.plus(int(39)).-<(
        int.is(int.gt(40)) --> int.plus(1) |
          int.is(int.gt(30)) --> int.plus(2) |
          int.is(int.gt(20)) --> int.plus(3) |
          int.is(int.gt(10)) --> int.plus(4)).>-.plus(1))

    assertResult(int(33))(
      int(0) ==> int.plus(29).-<(
        int.is(int.gt(40)) --> int.plus(1) |
          int.is(int.gt(30)) --> int.plus(2) |
          int.is(int.gt(20)) --> int.plus(3) |
          int.is(int.gt(10)) --> int.plus(4)).>-.plus(1))

    assertResult(int(33))(
      int(0) ==> int.plus(29).-<(
        int.is(__.gt(40)) --> int.plus(1) |
          int.is(__.gt(30)) --> int.plus(2) |
          int.is(__.gt(20)) --> int.plus(3) |
          int.is(__.gt(10)) --> int.plus(4)).>-.plus(int(1)))

    assertResult(int(32))(
      int(0) ===> int.plus(29).-<(
        int.is(int.gt(40)) --> __.plus(1) |
          int.is(int.gt(30)) --> __.plus(2) |
          int.is(int.gt(20)) --> __.plus(3) |
          int.is(int.gt(10)) --> __.plus(4)).>-)
  }

  test("[choice] w/ traverser state") {
    assertResult(real(2.0, 3.0, 3.0))(
      real(0.0, 1.0, 1.0) ===> real.q(3).to("x").plus(1.0).to("y").-<(
        __.is(__.eqs(1.0)) --> __.from("y") |
          __.is(__.eqs(2.0)) --> __.from("x")
      ).>-.plus(real.from("y")))
  }
}
