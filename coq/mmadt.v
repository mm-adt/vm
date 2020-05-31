(*
This mmadt.v source file for Coq version 8.11 (https://coq.inria.fr) 
provides a mechanically rigorous environment for understanding the mathematical 
properties of the corresponding JVM implementation of the mm-ADT VM.  Our goal
is to deduce features of mmlang in a purely axiomatic manner, based on the stream 
ring theory axioms for which mm-ADT VM was initialy founded, rather than the particulars 
of its Scala implementation, which must be optimized for e.g., speed, robustness, etc.
*)

(* Author: Ryan Wisnesky *)

Require Import String BinNums List Bool Floats BinIntDef ProofIrrelevance FunctionalExtensionality Sorted.
Import ListNotations.
Open Scope string_scope. Open Scope type_scope. Open Scope list_scope.

(* Category theory, copied from APG Coq *)

Record Category := newCategory {
 ob:Type; hom:ob->ob->Type; Id:forall {x}, hom x x;
 Comp : forall {x y z}, hom x y -> hom y z -> hom x z;
 catId1  : forall {x y} (f: hom x y), Comp Id f = f;
 catId2  : forall {x y} (f: hom x y), Comp f Id = f;
 catComp : forall {w x y z} (f: hom w x) g (h: hom y z), Comp f (Comp g h) = Comp (Comp f g) h;
}.

Definition Ob {C} := ob C. Definition Hom {C} x y := hom C x y. Definition id {C} x := @Id C x.
Definition comp {C} {x y z : ob C} (f: Hom x y) (g: Hom y z) := Comp C f g.

Definition terminal    {C} (x: ob C) := forall a, exists! (f: Hom a x), True.  
Definition initial     {C} (x: ob C) := forall a, exists! (f: Hom x a), True.
Definition product     {C} (X1 X2 X: ob C) (p1 : Hom X X1) (p2 : Hom X X2) := forall Y (f1 : Hom Y X1) (f2 : Hom Y X2), exists! (f: Hom Y X), comp f p1 = f1 /\ comp f p2 = f2.
Definition coproduct   {C} (X1 X2 X: ob C) (i1 : Hom X1 X) (i2 : Hom X2 X) := forall Y (f1 : Hom X1 Y) (f2 : Hom X2 Y), exists! (f: Hom X Y), comp i1 f = f1 /\ comp i2 f = f2.
Definition coequalizer {C} (X Y: ob C) (f g: Hom X Y) Q (q : Hom Y Q) := comp f q = comp g q /\ forall Q0 (q0 : Hom Y Q0),  comp f q0 = comp g q0 -> exists! (u: Hom Q Q0), comp q u = q0. 
Definition equalizer   {C} (X Y: ob C) (f g: Hom Y X) Q (q : Hom Q Y) := comp q f = comp q g /\ forall Q0 (q0 : Hom Q0 Y), comp q0 f = comp q0 g -> exists! (u: Hom Q0 Q), comp u q = q0.  

Record Functor C D := newFunctor {
 ApplyO:ob C -> ob D; ApplyF: forall {x y}, hom C x y -> hom D (ApplyO x) (ApplyO y);
 funId : forall {x}, ApplyF (id x) = id (ApplyO x);
 funComp : forall {x y z} (f: hom C x y) (g: hom C y z), ApplyF (comp f g) = comp (ApplyF f) (ApplyF g);
}.

Definition applyF {C D} (F: Functor C D) {x y} := @ApplyF C D F x y.
Definition applyO {C D} (F: Functor C D) := @ApplyO C D F .

Definition IdFunctor C : Functor C C. refine (newFunctor C C (fun x => x) (fun x y f => f) _ _); auto. Defined.

Definition CompFunctor {C D E} (F: Functor C D) (G: Functor D E) : Functor C E.
 refine (newFunctor C E (fun x => applyO G (applyO F x)) (fun _ _ f => applyF G (applyF F f)) _ _).
destruct F,G; compute in *; congruence. destruct F,G; compute in *; congruence. Defined.

Record Transform {C D} (F G : Functor C D) := newTransform {
 component : forall x, Hom (applyO F x) (applyO G x);
 natural : forall {x y} (f : hom C x y), comp (component x) (applyF G f) = comp (applyF F f) (component y);
}. (* Arguments newTransform [C D]. Arguments natural [C D F G]. Arguments component [C D F G].  *)

Definition Component {C D} {F G : Functor C D} x := @component C D F G x.
Lemma TransPfIrr {C D} {F G : Functor C D} (h1 h2 : Transform F G) : (Component h1 = Component h2) -> h1 = h2.
 intros pf. destruct h1,h2;simpl in *. subst. f_equal. apply proof_irrelevance. Qed.

Definition IdTrans {C D} (F: Functor C D) : Transform F F.
refine (newTransform _ _ F F (fun c => id (applyO F c)) _). intros.
unfold comp. rewrite (@catId1 D). rewrite (@catId2 D). constructor. 
Defined.

Definition CompTrans {C D} {F G H: @Functor C D} (h1 : Transform F G) (h2: Transform G H) : Transform F H.
refine (newTransform _ _ F H (fun c => comp (Component h1 c) (Component h2 c)) _).
intros. unfold comp. 
rewrite (@catComp D). rewrite <- (natural _ _ h1).
rewrite <- (catComp D). rewrite (natural _ _ h2).  rewrite (@catComp D).
constructor.
Defined.

Record AbelianGroup X := newAbelianGroup {
 rzero : X; rplus : X -> X -> X;  
 rid1  : forall f, rplus rzero f = f; rid2 : forall f, rplus f rzero = f;
 rassoc: forall f g h, rplus f (rplus g h) = rplus (rplus f g) h;
 rcomm : forall f g, rplus f g = rplus g f}.

Definition Ringoid (C: Category) := forall x y, AbelianGroup (hom C x y).

(* SRT ********************************************************** *)

Import ListNotations.


Section SRT.

Record DTO := newDTO {
  type : Type;
  le : type -> type -> Prop;
  le_dec : forall x y   , {le x y} + {le y x};
  eq_dec : forall x y:type, {x = y} + {x <> y};
  le_refl    : forall x y, x = y -> le x y;
  le_trans   : forall x y z, le x y -> le y z -> le x z;
  le_antisym : forall x y  , le x y -> le y x -> x = y;
}.

Variable ty : Type.
Variable tdec : forall t1 t2 : ty, {t1=t2}+{t1<>t2}.
Variable td : ty -> DTO.

Record Bag A := newBag { 
  stream : list (type (td A)); 
  pf_sorted : LocallySorted (le (td A)) stream;
}.
Arguments newBag [A] stream [pf_sorted]. Arguments stream {A}.

Definition bag_empty {A} : Bag A := @newBag _ _ (LSorted_nil _).

Definition bag_sng {A} (a:(type (td A))) : Bag A := @newBag _ _ (LSorted_cons1 _ a).

Fixpoint list_ins {A} (a:(type (td A))) (b:list (type (td A))) : list (type (td A)) := 
 match b with
  | [] => [a]
  | x :: y => if le_dec (td A) x a then x :: list_ins a y else a :: x :: y  
 end.  

Definition bag_cons {A} (a:(type (td A))) (b:Bag A) : Bag A. 
 refine (@newBag _  (list_ins a (stream b)) _).
 destruct b. simpl. induction stream0. constructor. simpl in *. 
 destruct (le_dec (td A) a0 a). simpl in *.
 destruct stream0. constructor. constructor. auto. 
 simpl in *. destruct (le_dec (td A) t a).
 constructor.  apply IHstream0. inversion pf_sorted0. subst; auto.
 inversion pf_sorted0. subst; auto.
 constructor. apply IHstream0. inversion pf_sorted0. subst; auto. auto.
 constructor. auto. auto.
Defined.

Definition bag_union {A} (b b':Bag A) : Bag A 
 := @fold_right _ _ bag_cons b (stream b').

Lemma ExPfIrr {A} (h1 h2 : Bag A)
 : (stream h1 = stream h2) -> h1 = h2.
Proof.
 intros pf. destruct h1,h2;simpl in *. subst. f_equal. 
 apply proof_irrelevance.
Qed.

Lemma lshelper {A} a stream0 : LocallySorted (le (td A)) (a :: stream0) ->
 LocallySorted (le (td A)) (stream0).
Proof.
 intros pf. inversion pf. subst; constructor; auto.
 subst. auto.
Qed.

Unset Printing Records.

Lemma bag_union_nil_l : forall {A} {b: Bag A} pf,
 bag_union (@newBag A [] pf) b = b.
Proof.
 intros. destruct b. apply ExPfIrr.  induction stream0.  auto.
 unfold bag_union in *. unfold bag_cons in *. unfold bag_empty in *. simpl in *.
 rewrite IHstream0. simpl. clear IHstream0.
 induction stream0. simpl; auto.
 inversion pf_sorted0. subst. simpl. 
 destruct (le_dec (td A) a0 a). 
 assert (a = a0). apply le_antisym; auto. subst.
 rewrite IHstream0. auto.
 inversion pf_sorted0. subst. auto. auto.
 inversion pf_sorted0. subst. constructor. 
 subst. auto.
Qed. 

Lemma bag_union_nil_r : forall {A} {b: Bag A} {pf},
 bag_union b (@newBag A [] pf) = b.
Proof.
 intros. destruct b. apply ExPfIrr. simpl. auto. 
Qed. 

Lemma leh {A} {a a0: type (td A)} (pf : le (td A) a0 a) Z (X Y : Z) : 
(if le_dec (td A) a0 a then X else Y) = X \/ a = a0.
Proof.
  destruct (le_dec (td A) a0 a). auto.
 assert (a0 = a). apply le_antisym; auto. subst. auto.
Qed.

Lemma bag_union_comm : forall {A} (a b:Bag A), 
 bag_union a b = bag_union b a.
Proof.
 intros A. destruct a. induction stream0.

 intros. rewrite bag_union_nil_l. rewrite bag_union_nil_r. auto.

 intros. simpl in *. pose (IHstream0 (lshelper _ _ pf_sorted0)).
 clearbody e. clear IHstream0.
 unfold bag_union in *. simpl in *.  
 destruct b; simpl in *. 
 rewrite <- e. simpl. clear e. induction stream1.
 simpl. apply ExPfIrr. simpl. 

 induction stream0. auto.
 simpl in *. destruct (le_dec (td A) a0 a). 
 inversion pf_sorted0. subst. 
 assert (a0 = a). apply le_antisym; auto. subst.
 rewrite IHstream0. auto. auto. auto.

 simpl in *. rewrite (IHstream1 (lshelper _ _ pf_sorted1)); auto. 
 generalize ((@fold_right (Bag A) (type (td A)) (@bag_cons A)
        (@newBag A stream0 (@lshelper A a stream0 pf_sorted0)) stream1)).
  intros. clear IHstream1 pf_sorted0 pf_sorted1. destruct b as [b' pf].

 apply ExPfIrr. simpl. induction b'. simpl.
 destruct (le_dec (td A) a a0); destruct (le_dec (td A) a0 a).
  assert (a = a0). apply le_antisym; auto. subst. auto. auto. auto.
  assert (a = a0). apply le_antisym; auto. subst. auto.

 simpl in *. destruct (eq_dec _ a a0). subst. auto.

 remember (le_dec _ a1 a) as x; destruct x; simpl.

 destruct (le_dec (td A) a1 a0); simpl in *.
  rewrite (IHb' (lshelper _ _ pf)). rewrite <- Heqx. auto.
  rewrite <- Heqx. destruct (le_dec _ a0 a); simpl in *. auto.
 assert (le _ a1 a0). eapply le_trans; eauto.
 assert (a0 = a1). apply le_antisym; auto. subst.
 assert (a = a1). apply le_antisym; auto. subst. contradiction.

 destruct (le_dec _ a a0); simpl in *. 
 destruct (le_dec (td A) a1 a0). simpl. rewrite <- Heqx. auto.
 simpl. rewrite <- Heqx.
 destruct (le_dec (td A) a0 a). assert (a = a0).
 apply le_antisym; auto. subst; auto. auto.

 destruct (le_dec (td A) a1 a0). simpl. 
 assert (a0 = a1). apply le_antisym. eapply le_trans; eauto. auto.
 subst. rewrite <- Heqx. assert (a = a1). apply (le_antisym); auto.
 subst. contradiction. simpl. rewrite <- Heqx.
 destruct (le_dec (td A) a0 a). simpl. 
 auto. assert (a = a0). apply le_antisym; auto. subst. auto.
Qed.

Definition bag_map {B A}  (f: type (td B) -> type (td A)) (b: Bag B) : Bag A :=
  @fold_right _ _ (fun x => bag_cons (f x)) bag_empty (stream b).

Definition bag_flatmap {A B} (f: type (td B) -> Bag A) (b: Bag B) : Bag A :=
  @fold_right _ _ (fun x => bag_union (f x)) bag_empty (stream b).

Definition StreamFn A B := type (td A) -> Bag B.

Lemma flat_map_sng {A} (x: list A) : flat_map (fun b => [b]) x = x.
Proof.
 induction x. simpl. auto. simpl in *. rewrite IHx. auto.
Qed.

Lemma newBag_cons y a x (pf : LocallySorted (le (td y)) (a :: x)) 
 : @newBag _ (a::x) pf = bag_cons a (@newBag _ x (lshelper _ _ pf)).
Proof.
 apply ExPfIrr. simpl. induction x. auto.
 simpl in *. destruct (le_dec (td y) a0 a). rewrite <- IHx.
 inversion pf. subst. assert (a = a0). apply le_antisym; auto. subst. auto.
 inversion pf. subst. assert (a = a0). apply le_antisym; auto. subst. auto. auto.
Qed.

Lemma cons_union_sng' {X} (b: Bag X) 
 : forall a, bag_cons a b = bag_union b (bag_sng a).
Proof.
 destruct b; induction stream0. auto.
  intros.
  rewrite newBag_cons. rewrite IHstream0.
  unfold bag_union. simpl. auto.
Qed.

Lemma bag_flatmap_zero {A B} (f: type (td B) -> Bag A) pf1 :
 bag_flatmap f (@newBag _ [] pf1) = bag_empty.
Proof.
 apply ExPfIrr. simpl. auto.
Qed.

Lemma bag_cons_comm' {X} a b (x:list (type (td X))) :
 list_ins a (list_ins b x) = list_ins b (list_ins a x).
Proof.
 induction x. simpl.
 destruct (le_dec (td X) b a); destruct (le_dec (td X) a b).
 assert (a = b). apply le_antisym; auto. subst. auto. auto. auto. 
 assert (a = b). apply le_antisym; auto. subst. auto. 
 destruct (eq_dec _ a b). subst; auto.
 simpl. remember (le_dec (td X) a0 b). destruct s. simpl.
 destruct (le_dec (td X) a0 a); simpl. rewrite <- Heqs. 
 f_equal. apply IHx. 
 rewrite <- Heqs. destruct (le_dec (td X) a b); simpl. auto.
 assert (le _ a0 a). eapply le_trans; eauto.
 assert (a = a0). apply le_antisym; auto. subst.
 assert (a0 = b). apply le_antisym; auto. subst. contradiction.
 simpl. destruct (le_dec (td X) b a). destruct (le_dec (td X) a0 a).
 simpl. rewrite <- Heqs. auto.
 simpl. rewrite <- Heqs. destruct (le_dec (td X) a b).
 assert (a = b).  apply le_antisym; auto. subst. auto. auto.
 simpl. destruct (le_dec (td X) a0 a). simpl. rewrite <- Heqs.
 assert (le _ b a). eapply le_trans; eauto.
 assert (a = b).  apply le_antisym; auto. subst. contradiction.
 simpl. rewrite <- Heqs. destruct (le_dec (td X) a b).
 auto. assert (a = b). apply le_antisym; auto. subst. contradiction.
Qed.

Lemma bag_cons_comm {X} a b (x:Bag X) :
 bag_cons a (bag_cons b x) = bag_cons b (bag_cons a x).
Proof.
 apply ExPfIrr. simpl. apply bag_cons_comm'.
Qed.


Lemma bag_union_cons {X} (b: Bag X) 
 : forall a c, bag_union (bag_cons a b) c = bag_cons a (bag_union b c).
Proof.
 destruct b. induction stream0.

 intros. rewrite bag_union_nil_l. rewrite cons_union_sng'. 
 rewrite bag_union_nil_l. rewrite cons_union_sng'.  apply bag_union_comm.
 
 intros. rewrite newBag_cons. rewrite IHstream0.
 clear IHstream0. unfold bag_union.
 repeat rewrite cons_union_sng'. unfold bag_union.
 simpl. destruct c. simpl.
 induction stream1. auto. simpl. 
 rewrite IHstream1. rewrite bag_cons_comm.
 f_equal.
 rewrite bag_cons_comm. f_equal. eapply lshelper; eauto.
Qed. 


Lemma bag_union_assoc : forall {A} (a b c:Bag A), 
 bag_union a (bag_union b c) = bag_union (bag_union a b) c.
Proof.
 intros. destruct a. induction stream0. repeat rewrite bag_union_nil_l. auto.
 repeat rewrite newBag_cons.  rewrite bag_union_cons.
 rewrite IHstream0. repeat rewrite <- bag_union_cons. auto.
Qed.

Lemma cons_union_sng {X} (b: Bag X) 
 : forall a, bag_cons a b = bag_union (bag_sng a) b.
Proof.
 intros. rewrite cons_union_sng'. apply bag_union_comm.
Qed. 


Lemma flat_map_nil {X Y}  (f:type (td X)->Bag Y) pf :
   bag_flatmap f (@newBag _ [] pf) = bag_empty.
Proof.
 apply ExPfIrr. simpl. auto.
Qed.

Lemma bag_flatmap_sng {X Y} a (f:type (td X)->Bag Y) 
 : (bag_flatmap f (bag_sng a)) = f a.
Proof.
 unfold bag_flatmap. simpl. apply bag_union_nil_r.
Qed.

Lemma bag_union_comm' {X} (a b c : Bag X) : bag_union a (bag_union b c) = 
 bag_union b (bag_union a c).
Proof.
 rewrite bag_union_assoc.
 rewrite bag_union_comm.
 rewrite bag_union_assoc.
 rewrite bag_union_comm. f_equal.
 apply bag_union_comm.
Qed.

Lemma bag_flatmap_cons : forall {X Y} a l (f:type (td X)->Bag Y),
 bag_flatmap f (bag_cons a l) = bag_union (f a) (bag_flatmap f l).
Proof.
 intros. destruct l. rewrite cons_union_sng.
 induction stream0. rewrite bag_union_nil_r. rewrite flat_map_nil.
 unfold bag_empty. rewrite bag_union_nil_r. apply ExPfIrr. auto.
 rewrite newBag_cons. rewrite <- cons_union_sng in *.
 pose (IHstream0 (lshelper _ _ pf_sorted0)).
 clearbody e. clear IHstream0. 
 rewrite <- cons_union_sng in e.

 destruct stream0. 
 unfold bag_cons in *. simpl in *.
   unfold bag_flatmap in *. simpl in *.
 unfold bag_empty.
 rewrite bag_union_nil_r. destruct (le_dec (td X) a0 a).
 simpl in *.  rewrite bag_union_nil_r. apply bag_union_comm.
 simpl in *.  rewrite bag_union_nil_r. auto.
 
 rewrite <- newBag_cons.  unfold 
bag_cons in *. simpl in *. unfold bag_flatmap.
 simpl in *. destruct (le_dec (td X) a0 a).
destruct (le_dec (td X) t a). simpl in *.
 unfold bag_flatmap in *. simpl in *. 
 symmetry. rewrite (bag_union_comm'). symmetry.
 f_equal. rewrite e. auto. 
 rewrite (bag_union_comm'). auto. simpl in *.  auto.
Qed.


Lemma bag_flatmap_union : forall {X Y} a b (f:type (td X)->Bag Y),
 bag_flatmap f (bag_union a b) = bag_union (bag_flatmap f a) (bag_flatmap f b).
Proof.
 intros X Y a. destruct a. 
 induction stream0. intros. rewrite bag_union_nil_l. rewrite flat_map_nil.
 unfold bag_empty. rewrite bag_union_nil_l. auto.

 intros. rewrite newBag_cons.
 rewrite bag_union_cons.  rewrite bag_flatmap_cons. rewrite bag_flatmap_cons.
 rewrite IHstream0. clear IHstream0.

destruct stream0. 
   unfold bag_flatmap in *. simpl in *.
 unfold bag_empty. rewrite bag_union_nil_l.
 rewrite bag_union_nil_r. auto. 
  inversion pf_sorted0. subst. 
    unfold bag_flatmap in *. simpl in *. 
 rewrite bag_union_assoc . auto.
Qed. 


Lemma bag_flatmap_sng' {X} (x: Bag X) 
 : bag_flatmap (fun z => bag_sng z) x = x.
Proof. 
 destruct x. induction stream0. rewrite flat_map_nil. unfold bag_empty.
 apply ExPfIrr; auto. destruct stream0. apply ExPfIrr. simpl. auto.
 inversion pf_sorted0. subst. 
 rewrite newBag_cons. rewrite bag_flatmap_cons. rewrite IHstream0. clear IHstream0.
 rewrite newBag_cons. rewrite <- cons_union_sng. auto.
Qed.

Lemma bag_flatmap_empty' {X Y} (x: Bag X) 
 : bag_flatmap (fun _ => bag_empty) x = @bag_empty Y.
Proof.
 destruct x. induction stream0. rewrite flat_map_nil. auto. 
 destruct stream0. apply ExPfIrr. simpl. auto.
 inversion pf_sorted0. subst.  
 rewrite newBag_cons. rewrite bag_flatmap_cons. rewrite IHstream0. clear IHstream0.
 unfold bag_empty. rewrite bag_union_nil_r. auto.
Qed.

Lemma bag_flatmap_empty {X Y} (f: type (td X) -> Bag Y) pf pf'
 : bag_flatmap f (@newBag _ [] pf) = (@newBag _ [] pf').
Proof.
 apply ExPfIrr. auto.
Qed.

Definition fnComp {X Y Z} (f:Y->Z) (g:X->Y) x := f (g x).

Lemma fold_right_cons : forall X Y (a:X) (o:Y) (l:list Y) (f:Y->X->X),
 @fold_right X Y f a (o::l) = f o (fold_right f a l).
Proof.
 auto.
Qed.

Lemma equal_f {X Y} : forall {x x':X} (pf: x = x') (f:X->Y), 
  f x = f x'.
Proof.
 intros.
 congruence.
Qed.

Lemma bag_map_cons : forall {X Y} (f:type (td X)->type (td Y)) l a,
 bag_map f (bag_cons a l) = bag_cons (f a) (bag_map f l).
Proof.
 intros X Y f.
 destruct l. induction stream0. auto. 
 
 intros.  unfold bag_map at 2. unfold stream.
 rewrite fold_right_cons.

 destruct stream0. apply ExPfIrr. simpl. unfold bag_empty. simpl.
 unfold bag_cons. simpl. unfold bag_map. simpl. 
 destruct (le_dec (td X) a a0 ); simpl; auto.
 destruct (le_dec (td Y) (f a0) (f a));
 destruct (le_dec (td Y) (f a) (f a0)); auto.
 assert (eq (f a0) (f a)). apply le_antisym; auto. rewrite H; auto.
 assert (eq (f a0) (f a)). apply le_antisym; auto. rewrite H; auto.

 inversion pf_sorted0. subst.
 unfold bag_map, bag_cons in *. simpl in *.
 pose (IHstream0 H1 a0).
 clearbody e. clear IHstream0.
 destruct (le_dec (td X) t a0 ); simpl in *; auto;
 destruct (le_dec (td X) a a0 ); simpl in *; auto.

 pose (equal_f e stream). simpl in e0. clearbody e0. clear e. 
 apply ExPfIrr. simpl. rewrite e0. 
 rewrite bag_cons_comm'. auto.

 pose (equal_f e stream). simpl in e0. clearbody e0. clear e. 
 apply ExPfIrr. simpl. 
 rewrite bag_cons_comm'. auto.
Qed.

Lemma bag_map_sng : forall {X Y} (f:type (td X)->type (td Y)) a,
 bag_map f (bag_sng a) = bag_sng (f a).
Proof.
 auto.
Qed.

Lemma bag_flatmap_map {X Y Z} (f:type (td Y) -> Bag Z) (g:type (td X) -> type (td Y)) (x: Bag X) 
 : bag_flatmap f (bag_map g x) = bag_flatmap (fnComp f g) x.
Proof.
 destruct x. induction stream0. rewrite flat_map_nil. apply ExPfIrr. auto.
 rewrite newBag_cons. rewrite bag_flatmap_cons. 
 rewrite <- IHstream0. clear IHstream0.
 destruct stream0. apply ExPfIrr. simpl. auto.
 inversion pf_sorted0. subst.  
 rewrite newBag_cons. rewrite bag_map_cons. rewrite bag_map_cons.
 rewrite bag_flatmap_cons. auto.
Qed.

Lemma bag_map_empty : forall {X Y} (f:type (td X) -> type (td Y)) pf pf',
 bag_map f (@newBag _ [] pf) = @newBag _ [] pf'.
Proof.
 intros. apply ExPfIrr. simpl. auto.
Qed.


Lemma bag_map_union : forall {X Y} (f:type (td X)->type (td Y)) a b,
 bag_map f (bag_union a b) = bag_union (bag_map f a) (bag_map f b).
Proof.
  intros X Y f.
 destruct a. induction stream0. intros. rewrite bag_union_nil_l.
 erewrite (bag_map_empty). rewrite bag_union_nil_l. auto.
 
 intros. rewrite newBag_cons. rewrite bag_union_cons.
 rewrite bag_map_cons. rewrite IHstream0. rewrite bag_map_cons.
 rewrite bag_union_cons. auto.
 Unshelve. constructor.
Qed.
 

Definition mathcalF' : Category. 
 refine (newCategory ty StreamFn (@bag_sng) (fun _ _ _ f g x => bag_flatmap g (f x)) _ _ _); simpl; intros. 

 apply functional_extensionality. intros. apply  (bag_union_nil_r).

 apply functional_extensionality. intros. 
  generalize (f x0). intros b; destruct b.
 induction stream0; unfold bag_flatmap. simpl. apply ExPfIrr. simpl. auto.
 simpl. rewrite newBag_cons. rewrite <- IHstream0. rewrite cons_union_sng. auto.
 
 apply functional_extensionality. intros t. 
 destruct ((f t)). induction stream0. rewrite bag_flatmap_zero.
 rewrite bag_flatmap_zero. symmetry. apply bag_flatmap_zero.
 
 rewrite newBag_cons. rewrite bag_flatmap_cons. 
  rewrite IHstream0. clear IHstream0. rewrite bag_flatmap_cons.
 rewrite bag_flatmap_union. auto.
Defined.

Definition mathcalF : Ringoid mathcalF'.
 refine (fun (X Y: ty) => newAbelianGroup (hom mathcalF' X Y) (fun _ => bag_empty) (fun f g c => bag_union (f c) (g c)) _ _ _ _); intros; simpl in *; auto. 
 apply functional_extensionality. intros. apply bag_union_nil_l.
 apply functional_extensionality. intros. apply bag_union_assoc.
 apply functional_extensionality. intros. apply bag_union_comm.
Qed.

End SRT.




Check (mathcalF' Type). (*  : forall (td : Type -> DTO), Category *)
Check (mathcalF  Type). (*  : forall (td : Type -> DTO), Ringoid (mathcalF' ty td) *)

Lemma emptySetInitial : forall td (pf: forall x, type (td x) = x), 
 @initial (mathcalF' Type td) Empty_set.
Proof.
 unfold initial. intros. 
 unfold unique. unfold Hom. simpl. unfold StreamFn.
 rewrite pf. simpl in *. refine (ex_intro _ (fun t:Empty_set => match t with end) _).
 split. auto. intros. 
 apply functional_extensionality. intros x. elim x.
Qed.

Lemma emptySetTerminal : forall td (pf: forall x, type (td x) = x), 
  @terminal (mathcalF' Type td) Empty_set .
Proof.
 unfold terminal. 
 unfold unique. unfold Hom. simpl. unfold StreamFn.
 intros. rewrite (pf a). refine (ex_intro _ (fun t => bag_empty Type td) _).
 split; auto. intros.
 apply functional_extensionality. intros x.
 apply ExPfIrr. simpl. destruct (x' x). simpl in *.
 destruct stream0. auto. 
 destruct stream0. contradiction (match  (pf _) in _ = a' return a' with refl_equal => t end).
contradiction (match  (pf _) in _ = a' return a' with refl_equal => t end).
Qed. 

Definition cast {X} x {Y} (pf: X = Y) : Y :=
 match pf in _ = Y0 return Y0 with
  | refl_equal => x
 end.

Definition cast' {X} x {Y} (pf: Y = X) : Y :=
 match eq_sym pf in _ = Y0 return Y0 with
  | refl_equal => x
 end.

Lemma cast_cast' {X Y} x (pf1 pf2: Y = X)  :
 x = cast (cast' x pf1) pf2.
Proof.
 assert (pf1 = pf2). apply proof_irrelevance. subst. compute. auto.
Qed.

Lemma cast'_cast {X Y} x (pf1 pf2: Y = X)  :
 x = cast' (cast x pf1) pf2.
Proof.
 assert (pf1 = pf2). apply proof_irrelevance. subst. compute. auto.
Qed. 

Lemma disjointUnionCoProduct : forall td (pf: forall {x}, type (td x) = x) A B, 
 @coproduct (mathcalF' Type td) A B (A+B) 
   ( (fun z => bag_sng _ _ (cast' (inl (cast z pf)) pf))) 
   ( (fun z => bag_sng _ _ (cast' (inr (cast z pf)) pf))).
Proof.
 unfold coproduct. 
 unfold unique. unfold Hom. simpl. unfold StreamFn.
 intros td pf A. rewrite (pf A). intros B. rewrite (pf B). simpl.
 intros. exists (fun x => match (cast x (pf (A+B))) with inl z => f1 z | inr z => f2 z end). split. split. 
 apply functional_extensionality. intros a.
 rewrite bag_flatmap_sng. erewrite <- cast_cast'. auto.
 apply functional_extensionality. intros b.
 rewrite bag_flatmap_sng. erewrite <- cast_cast'. auto.
 intros. destruct H. subst. 
 apply functional_extensionality. intros x.
 rewrite <- (@bag_flatmap_sng Type td _ _ x x').
 remember (cast x (pf (A + B))). destruct s.
 rewrite Heqs. erewrite <- cast'_cast. auto.
 rewrite Heqs. erewrite <- cast'_cast. auto.
Qed.


Lemma disjointUnionProduct : forall td (pf: forall {x}, type (td x) = x) A B, 
 @product (mathcalF' Type td) A B (A+B) 
   ( (fun z => match cast z pf with inl z0 => bag_sng _ _ (cast' z0 pf) | inr z0 => bag_empty _ _  end ) )
   ( (fun z => match cast z pf with inr z0 => bag_sng _ _ (cast' z0 pf) | inl z0 => bag_empty _ _  end )).
Proof.
 unfold product. 
 unfold unique. unfold Hom. simpl. unfold StreamFn.
 
 intros.
 exists (fun x => @bag_union _ _ ((A+B))
  (@bag_map _ _ A (A+B) (fun z => cast' (@inl A B (cast z (pf A))) (pf _)) (f1 x))
  (@bag_map _ _ B (A+B) (fun z => cast' (@inr A B (cast z (pf B))) (pf _)) (f2 x))
 ). 
 split. split.

 apply functional_extensionality. intros a.
 rewrite bag_flatmap_union.
 rewrite bag_flatmap_map. rewrite bag_flatmap_map. unfold fnComp. 
 assert ((fun x : type (td A) =>
      match
        @cast (type (td (A + B)))
          (@cast' (A + B)
             (@inl A B (@cast (type (td A)) x A (pf A)))
             (type (td (A + B))) (pf (A + B))) 
          (A + B) (pf (A + B))
      with
      | inl z0 =>
          @bag_sng Type td A
            (@cast' A z0 (type (td A)) (pf A))
      | inr _ => @bag_empty Type td A
      end) = (fun x : type (td A) =>
      match
         (@inl A B (@cast (type (td A)) x A (pf A)))    
      with
      | inl z0 =>
          @bag_sng Type td A
            (@cast' A z0 (type (td A)) (pf A))
      | inr _ => @bag_empty Type td A
      end)).
 apply functional_extensionality. intros. erewrite <- cast_cast'. auto.
 rewrite H. clear H. assert ((fun x : type (td B) =>
      match
        @cast (type (td (A + B)))
          (@cast' (A + B)
             (@inr A B (@cast (type (td B)) x B (pf B)))
             (type (td (A + B))) (pf (A + B))) 
          (A + B) (pf (A + B))
      with
      | inl z0 =>
          @bag_sng Type td A
            (@cast' A z0 (type (td A)) (pf A))
      | inr _ => @bag_empty Type td A
      end) = (fun x : type (td B) =>
      match
        (@inr A B (@cast (type (td B)) x B (pf B)))
      with
      | inl z0 =>
          @bag_sng Type td A
            (@cast' A z0 (type (td A)) (pf A))
      | inr _ => @bag_empty Type td A
      end)).
 apply functional_extensionality. intros. erewrite <- cast_cast'. auto.
 rewrite H. clear H. assert ((fun x : type (td A) =>
      @bag_sng Type td A
        (@cast' A (@cast (type (td A)) x A (pf A))
           (type (td A)) (pf A))) = (fun x : type (td A) =>
      @bag_sng Type td A
         x )). 
 apply functional_extensionality. intros. erewrite <- cast'_cast. auto.
 rewrite H. clear H. rewrite bag_flatmap_empty'. unfold bag_empty.
 rewrite bag_union_nil_r. clear f2 B . rewrite bag_flatmap_sng'. auto.



  apply functional_extensionality. intros a.
 rewrite bag_flatmap_union.
 rewrite bag_flatmap_map. rewrite bag_flatmap_map. unfold fnComp. 
 assert ((fun x : type (td A) =>
      match
        @cast (type (td (A + B)))
          (@cast' (A + B) (@inl A B (@cast (type (td A)) x A (pf A)))
             (type (td (A + B))) (pf (A + B))) (A + B) 
          (pf (A + B))
      with
      | inl _ => @bag_empty Type td B
      | inr z0 => @bag_sng Type td B (@cast' B z0 (type (td B)) (pf B))
      end) = fun x : type (td A) =>
      match
        (@inl A B (@cast (type (td A)) x A (pf A)))
      with
      | inl _ => @bag_empty Type td B
      | inr z0 => @bag_sng Type td B (@cast' B z0 (type (td B)) (pf B))
      end).  apply functional_extensionality. intros. erewrite <- cast_cast'. auto.
 rewrite H. clear H. assert (
(fun x : type (td B) =>
      match
        @cast (type (td (A + B)))
          (@cast' (A + B) (@inr A B (@cast (type (td B)) x B (pf B)))
             (type (td (A + B))) (pf (A + B))) (A + B) 
          (pf (A + B))
      with
      | inl _ => @bag_empty Type td B
      | inr z0 => @bag_sng Type td B (@cast' B z0 (type (td B)) (pf B))
      end) = (fun x : type (td B) =>
      match
         (@inr A B (@cast (type (td B)) x B (pf B)))
      with
      | inl _ => @bag_empty Type td B
      | inr z0 => @bag_sng Type td B (@cast' B z0 (type (td B)) (pf B))
      end)
).  apply functional_extensionality. intros. erewrite <- cast_cast'. auto.
 rewrite H. clear H. 
 rewrite bag_flatmap_empty'. unfold bag_empty.
 rewrite bag_union_nil_l.
 assert ((fun x : type (td B) =>
   @bag_sng Type td B
     (@cast' B (@cast (type (td B)) x B (pf B)) (type (td B)) (pf B))) =
(fun x : type (td B) =>
   @bag_sng Type td B
     ( x ))).  apply functional_extensionality. intros. erewrite <- cast'_cast. auto.
 rewrite H. clear H. rewrite bag_flatmap_sng'. auto.

 intros. destruct H. subst.  apply functional_extensionality. intros.
 
 destruct (x' x). induction stream0. 
 rewrite flat_map_nil. rewrite flat_map_nil. unfold bag_empty.
 erewrite bag_map_empty. erewrite bag_map_empty. apply ExPfIrr. simpl. auto.
 
 destruct stream0. rewrite newBag_cons. clear IHstream0. 
rewrite bag_flatmap_cons.
 rewrite flat_map_nil. unfold bag_empty. rewrite bag_union_nil_r.
 rewrite bag_flatmap_cons. rewrite flat_map_nil. unfold bag_empty.
 rewrite bag_union_nil_r. 
 remember (cast a (pf (A + B))). 
 destruct s. 
  erewrite bag_map_empty. unfold bag_map. unfold bag_sng. simpl. unfold bag_union. simpl.
  unfold bag_empty. simpl. f_equal.
  rewrite <- cast_cast'. simpl. rewrite Heqs. rewrite <- cast'_cast. auto.
  apply ExPfIrr. auto. 
  erewrite bag_map_empty.
  unfold bag_map. unfold bag_sng. simpl. unfold bag_union. simpl.
  unfold bag_empty. simpl. f_equal.
  rewrite <- cast_cast'. simpl. rewrite Heqs. rewrite <- cast'_cast. auto.

 rewrite newBag_cons.
rewrite bag_flatmap_cons. rewrite bag_flatmap_cons.
rewrite bag_map_union.  rewrite bag_map_union.
 rewrite  bag_union_assoc.
remember (cast a (pf (A + B))).  destruct s. 
 rewrite bag_map_sng.
 rewrite <- cons_union_sng. rewrite bag_union_cons. rewrite <- cast_cast'. 
 rewrite bag_union_cons. f_equal. rewrite Heqs. rewrite <- cast'_cast. auto.

 unfold bag_empty. erewrite bag_map_empty. rewrite bag_union_nil_r. auto.
 unfold bag_empty. erewrite bag_map_empty. rewrite bag_union_nil_l.
 rewrite bag_map_sng.  erewrite <- cast_cast'.
 rewrite <- cons_union_sng' . rewrite bag_union_cons. f_equal.
 rewrite Heqs. rewrite <- cast'_cast. auto. auto.
 Unshelve. constructor. Unshelve. constructor.
 Unshelve. constructor. Unshelve. constructor.
 Unshelve. constructor. 
Qed.

(* Construct a function Type -> DTO via non-constructive methods *)

Require Import Relation_Definitions.

Section WellOrder.

Variable T:Type.

Definition total_strict_order  (R: T->T->Prop) : Prop :=
 forall x y:T, R x y \/ x = y \/ R y x.

Record well_order (R:relation T) : Prop := {
 wo_well_founded: well_founded R;
 wo_total_strict_order: total_strict_order R
}.


Lemma wo_antisym: forall R:relation T, well_order R ->
  (forall x y:T, R x y -> ~ R y x).
Proof.
intuition. assert (forall z:T, Acc R z -> z <> x /\ z <> y).
intros. induction H2. intuition. rewrite H4 in H3.
pose proof (H3 y H1). tauto. rewrite H4 in H3. pose proof (H3 x H0). tauto.
pose proof (wo_well_founded R H). unfold well_founded in H3. pose proof (H2 x (H3 x)). tauto.
Qed.

Lemma wo_transitive: forall R:relation T, well_order R -> transitive _ R.
Proof.
intros. unfold transitive.
intros. case (wo_total_strict_order R H x z).
trivial. intro. case H2. intro.
rewrite H3 in H0. pose proof (wo_antisym R H y z).
contradict H0. auto.
intro.
assert (forall a:T, Acc R a -> a <> x /\ a <> y /\ a <> z).
intros. induction H4.
intuition. rewrite H2 in H5. pose proof (H5 z H3).
tauto. rewrite H2 in H5.
pose proof (H5 x H0).
tauto. rewrite H2 in H5.
pose proof (H5 y H1). tauto.
rewrite H2 in H5. pose proof (H5 z H3).
tauto. rewrite H2 in H5.
pose proof (H5 x H0). tauto.
rewrite H2 in H5. pose proof (H5 y H1).
tauto. pose proof (wo_well_founded R H).
unfold well_founded in H5.
pose proof (H4 x (H5 x)).
tauto.
Qed. 

Axiom zorn: exists R:relation T, well_order R.

End WellOrder.


Axiom constructive_indefinite_description :
  forall (A : Type) (P : A->Prop),
    (exists x, P x) -> { x : A | P x }.

Axiom em: forall (P : Prop), {P} + {~ P}.

Lemma xxx P Q :
 P \/ Q  -> {P} + {Q} .
 intros. pose (em P). destruct s. left; auto. intuition.
Defined.

Lemma xxx' P Q R :
 P \/ Q \/ R  -> {P} + {Q} + {R} .
 intros. pose (em P). destruct s. left; auto. intuition.
 pose (em Q). destruct s. left; auto. intuition.
Qed.

Lemma theChosenDTO'  t : 
 { dt : DTO | type dt = t }.
 pose (constructive_indefinite_description _ _ (zorn t)).
 destruct s.   
 simple refine (exist _ (@newDTO t (fun a b => (x a b \/ a = b)) _ _ _ _ _) _ ). 
 intros. simpl. destruct (xxx _ _ (wo_total_strict_order _ x w x0 y)).
 left; auto. right; auto. intuition. intros. exact (em (x0 = y)).
 intros. simpl. auto. intros. simpl in *. destruct H. destruct H0.
 left.  exact (wo_transitive _ _ w _ _ _ H H0). subst. left; auto. subst. auto.
 intros. simpl in *. destruct H. destruct H0.
elim (wo_antisym _ _ w _ _ H H0). auto. auto.
 simpl. auto.
Qed.

Definition theChosenDTO t : DTO := proj1_sig (theChosenDTO' t).

Lemma theChosenDTOIsOk : 
  (forall x, type (theChosenDTO x) = x).
Proof.
 intros. unfold theChosenDTO. destruct (theChosenDTO' x).
 simpl. auto.
Qed.

Definition mathcalFForReal := mathcalF Type (theChosenDTO ).
Check mathcalFForReal.
(*
mathcalFForReal
     : Ringoid (mathcalF' Type theChosenDTO)
*)
Print Assumptions mathcalFForReal.
(*
Axioms:
zorn : forall T : Type, exists R : relation T, well_order T R
proof_irrelevance : forall (P : Prop) (p1 p2 : P), p1 = p2
functional_extensionality_dep : forall (A : Type) (B : A -> Type)
                                  (f g : forall x : A, B x),
                                (forall x : A, f x = g x) -> f = g
em : forall P : Prop, {P} + {~ P}
constructive_indefinite_description : forall (A : Type) (P : A -> Prop),
                                      (exists x : A, P x) -> {x : A | P x}
*)
