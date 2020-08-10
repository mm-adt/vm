-- Haskell executable specification (naive but formal implementation) of mm-lang
-- using multi-sorted equational logic and re-writing, by Ryan Wisnesky.
-- In principle and practice, much of this code can be auto-generated from the
-- mmlang grammar and type system; the goal of this file is to bootstrap such
-- a process by establishing a human-written -- and understandable -- ground truth.

-- Right now, the REPL is parameterized by a list of re-write rules (representing
-- the mm-lang semantics), and it simply re-writes by the rules into all possible
-- results, the idea being to create a testbed to analyze the rules themselves.
-- For example, we can now test for confluence, etc. (some of which is done by
-- the Data.Rewriting package itself.)

module Main where
import System.IO
import Data.Maybe
import Control.Monad
import Text.ParserCombinators.Parsec
import Text.ParserCombinators.Parsec.Expr
import Text.ParserCombinators.Parsec.Language
import qualified Text.ParserCombinators.Parsec.Token as Token
import Data.List 
import Data.Rewriting.Term hiding (map, parse)
import Data.Rewriting.Rules hiding (map)
import Data.Rewriting.Rule hiding (map)
-----------------------------------------------
-- EMM-lang examples

{-- examples:
mmlang> int[plus,1][plus,2][plus,3]                                   
==>int[plus,1][plus,2][plus,3]

mmlang> int[plus,1][plus,2][plus,3][path]                             
==>(int;int[plus,1];int[plus,1][plus,2];int[plus,1][plus,2][plus,3])

mmlang> 1=>int=>[plus,1]=>[plus,2]=>[plus,3]=>int                     
==>7

mmlang> 1=>int[plus,1][plus,2][plus,3][path]                          
==>(1;2;4;7)

mmlang> 1=>int[plus,1][plus,2][plus,3][path]>-                        
==>7
--}

ex1 = "int[plus,1]" --ok
ex2 = "int[plus,1][plus,2][plus,3][path]" --ok
ex3 = "1=>int=>[plus,1]=>[plus,2]=>[plus,3]=>int" -- => not in cfg
ex4 = "1=>int[plus,1][plus,2][plus,3][path]" -- ibid
ex5 = "1=>int[plus,1][plus,2][plus,3][path]>-" -- ibid

test = case parse objParser "" ex2 of
  Right y -> eval mmAxioms (convObj y)

------------------------------------------------
-- Read-eval-print loop

mmAxioms = [] --todo

main = repl mmAxioms

repl rules = do { putStr "mmlang> ";
  x <- readLn;
  ep x;
  repl rules }
 where ep x = case (parse objParser "" x) of
                       Left err -> putStrLn $ show err 
                       Right xs -> putStrLn $ "==>" ++ (eval rules $ convObj xs) 

type MMRule = Rule Sym String

eval :: [MMRule] -> MMTerm -> String 
eval rs t = pre ++ (g 8 $ map (f 8) ts) 
 where (b, ts) = eval0 rs [[t]] 8
       pre = if b then "" else "WARNING: NO CONVERGENCE AFTER 8 ROUNDS\n"
       f _ [] = ""
       f 0 _ = "..."
       f n (a:b) = show (convObj' a) ++ " <~~ " ++ f (n-1) b  
       g _ [] = ""
       g 0 _ = "..."
       g n (a:b) = a ++ "\n" ++ g (n-1) b
       
eval0 :: [MMRule] -> [[MMTerm]] -> Integer -> (Bool, [[MMTerm]])
eval0 rs ts 0 = (False, ts)
eval0 rs ts n = if ts == ts' then (True, ts') else eval0 rs ts' (n-1)
 where ts' = eval' rs ts
 
eval' :: [MMRule] -> [[MMTerm]] -> [[MMTerm]]
eval' rs ts = concat $ map (eval'' rs) ts
 

eval'' :: [MMRule] -> [MMTerm] -> [[MMTerm]]
eval'' rs (h:t) | length next > 0 = [ (result x) : (h:t) | x <- next ]
                | otherwise = [(h:t)]
 where next = fullRewrite rs h
   
-- https://willamette.edu/~fruehr/haskell/evolution.html

-------------------------------------------------------------------------------
-- mmlang as a multi-sorted equational theory 
-- (suitable for e.g., embedding in the simply lambda calculus and CQL)

data Sym = TYPEOBJ | VALUEOBJ | BOOLVALUE Bool | INTVALUE Integer | STRVALUE String 
 | CTYPETYPE | DTYPETYPE | BOOLTYPE | POLYTYPE | ANONTYPE
 | INTTYPE | STRTYPE | DTYPE0 | DTYPE1 | SEPSEMI | SEPCOMMA | SEPBAR
 | LST' | REC' | INST' | LSTPOLY | RECPOLY | INSTPOLY | OP' Op {-- share for expediency --} 
 | INSTLISTNIL  | INSTLISTCONS  | SEPOBJLISTNIL | SEPOBJLISTCONS
 | SEPOBJOBJLISTNIL | SEPOBJOBJLISTCONS | OBJLISTNIL | OBJLISTCONS 
 deriving (Show, Eq, Ord)

data Sort = OBJ | VALUE | TYPE | CTYPE | DTYPE | SEP | LST | REC | 
 INST | POLY | OP | INSTLIST | SEPOBJLIST | SEPOBJOBJLIST | OBJLIST 
 deriving (Show, Eq, Ord)

type MMTerm = Term Sym String 

mmTypeOf = typeOf mmSig 

mmSig x = case x of
 TYPEOBJ -> ([TYPE], OBJ)
 VALUEOBJ -> ([VALUE], OBJ) 
 BOOLVALUE _ -> ([], VALUE)  
 INTVALUE _ -> ([], VALUE)
 STRVALUE _ -> ([], VALUE)
 CTYPETYPE -> ([CTYPE], TYPE) 
 DTYPETYPE -> ([DTYPE], TYPE)
 BOOLTYPE -> ([], CTYPE) 
 POLYTYPE -> ([], CTYPE)
 ANONTYPE -> ([], CTYPE)
 INTTYPE -> ([], CTYPE)
 STRTYPE -> ([], CTYPE)
 DTYPE0 -> ([CTYPE, INSTLIST], DTYPE)
 DTYPE1 -> ([CTYPE, CTYPE, INSTLIST], DTYPE) 
 SEPSEMI -> ([], SEP)
 SEPCOMMA -> ([], SEP)
 SEPBAR -> ([], SEP) 
 LST' -> ([OBJ, SEPOBJLIST], LST)
 REC' -> ([OBJ, OBJ, SEPOBJOBJLIST], REC)
 INST' -> ([OP, OBJLIST], INST)
 LSTPOLY -> ([LST], POLY)
 RECPOLY -> ([REC], POLY)
 INSTPOLY -> ([INST], POLY)
 OP' _ -> ([], OP) 
 INSTLISTNIL -> ([], INSTLIST)
 INSTLISTCONS -> ([INST, INSTLIST], INSTLIST)
 OBJLISTNIL -> ([], OBJLIST)
 OBJLISTCONS -> ([OBJ, OBJLIST], OBJLIST)
 SEPOBJLISTNIL -> ([], SEPOBJLIST)
 SEPOBJLISTCONS -> ([SEP, OBJ, SEPOBJLIST], SEPOBJLIST)
 SEPOBJOBJLISTNIL -> ([], SEPOBJOBJLIST)
 SEPOBJOBJLISTCONS -> ([SEP, OBJ, OBJ, SEPOBJOBJLIST], SEPOBJOBJLIST)

--anomaly = error "Anomaly, please report"

convObj' ::  MMTerm -> Obj 
convObj' (Var x) = ObjVar x  
convObj' (Fun f [a]) = case f of
  TYPEOBJ ->  TypeObj $ convType' a  
  VALUEOBJ -> ValueObj $ convValue' a 
 
convValue' :: MMTerm -> Value 
convValue' (Var x) = ValueVar x
convValue' (Fun f []) = case f of
  BOOLVALUE y -> BoolValue y  
  INTVALUE y -> IntValue y 
  STRVALUE y -> StrValue y 

convType' :: MMTerm -> Type
convType' (Var x) = TypeVar x 
convType' (Fun f [a]) = case f of 
  CTYPETYPE -> CTypeType $ convCType' a  
  DTYPETYPE -> DTypeType $ convDType' a 
 
convCType' :: MMTerm -> CType
convCType' (Var x) = CTypeVar x
convCType' (Fun f []) = case f of 
 BOOLTYPE -> BoolType
 POLYTYPE -> PolyType
 ANONTYPE -> AnonType
 INTTYPE -> IntType
 STRTYPE -> StrType

-- CType (Maybe CType) [Inst]
convDType' :: MMTerm -> DType 
convDType' (Var x) = DTypeVar x 
convDType' (Fun f ts) = case f of  
 DTYPE0 -> case ts of
   [c1,i] -> DType (convCType' c1) Nothing (convInstList' i) 
  -- x -> error $ "foo" ++ (show $ length x)
 DTYPE1 -> case ts of
   [c1,c2,i] -> DType (convCType' c1) (Just $ convCType' c2) (convInstList' i) 
  -- x -> error $ sep (map show x) "\n"

convSep' :: MMTerm -> Sep 
convSep' (Var x) = SepVar x
convSep' (Fun f []) = case f of 
 SEPSEMI -> SepSemi 
 SEPCOMMA -> SepComma 
 SEPBAR -> SepBar

convLst' :: MMTerm -> Lst  
convLst' (Var x) = LstVar x
convLst' (Fun f [a,b]) = case f of 
 LST' -> Lst (convObj' a) $ convSepObjList' b
 
convRec' :: MMTerm -> Rec
convRec' (Var x) = RecVar x
convRec' (Fun f [o1,o2,x]) = case f of 
 REC' -> Rec (convObj' o1) (convObj' o2) $ convSepObjObjList' x

convInst' :: MMTerm -> Inst  
convInst' (Var x) = InstVar x 
convInst' (Fun f [op, x]) = case f of 
 INST' -> Inst (convOp' op) $ convObjList' x
 
convPoly' :: MMTerm -> Poly  
convPoly' (Var x) = PolyVar x
convPoly' (Fun f [a]) = case f of
  LSTPOLY -> LstPoly $ convLst' a 
  RECPOLY -> RecPoly $ convRec' a 
  INSTPOLY -> InstPoly $ convInst' a 

convOp' :: MMTerm -> Op 
convOp' (Var x) = OpVar x 
convOp' (Fun f []) = case f of 
  OP' x -> x 
 
convInstList' :: MMTerm -> [Inst]  
convInstList' (Fun f x) = case f of
 INSTLISTNIL -> []
 INSTLISTCONS -> case x of 
   [a,b] -> (convInst' a) : convInstList' b 
 z -> error $ show z
 
convObjList' :: MMTerm -> [Obj]  
convObjList' (Fun f x) = case f of
 OBJLISTNIL -> []
 OBJLISTCONS -> case x of 
   [a,b] -> (convObj' a) : convObjList' b
 
convSepObjList' :: MMTerm -> [(Sep,Obj)] 
convSepObjList' (Fun f x) = case f of
 SEPOBJLISTNIL -> []
 SEPOBJLISTCONS -> case x of
   [a,b,c] -> (convSep' a, convObj' b) : convSepObjList' c
 
convSepObjObjList' :: MMTerm -> [(Sep,Obj,Obj)]
convSepObjObjList' (Fun f x) = case f of
 SEPOBJOBJLISTNIL -> []
 SEPOBJOBJLISTCONS -> case x of
   [a,b,c,d] -> (convSep' a, convObj' b, convObj' c) : convSepObjObjList' d

-- reverse direction (multi-sorted equational theory form to abstract syntax)

convObj :: Obj -> MMTerm 
convObj x = case x of
  ObjVar y -> Var y  
  TypeObj y -> Fun TYPEOBJ [convType y]
  ValueObj y -> Fun VALUEOBJ [convValue y]

convValue :: Value -> MMTerm 
convValue x = case x of  
  ValueVar y -> Var y
  BoolValue y -> Fun (BOOLVALUE y) [] 
  IntValue y -> Fun (INTVALUE y) []
  StrValue y -> Fun (STRVALUE y) []
      
convType :: Type -> MMTerm 
convType x = case x of
  TypeVar y -> Var y   
  CTypeType y -> Fun CTYPETYPE [convCType y]
  DTypeType y -> Fun DTYPETYPE [convDType y]

convCType :: CType -> MMTerm 
convCType x = case x of 
  CTypeVar y -> Var y
  BoolType -> Fun BOOLTYPE []
  PolyType -> Fun POLYTYPE []
  AnonType -> Fun ANONTYPE []
  IntType -> Fun INTTYPE [] 
  StrType -> Fun STRTYPE []
  
convDType :: DType -> MMTerm 
convDType (DTypeVar v) = Var v 
convDType (DType c x insts) = case x of
  Nothing -> Fun DTYPE0 $ [convCType c, g insts]
  Just y -> Fun DTYPE1 $ [convCType c, convCType y, g insts]
  where g [] = Fun INSTLISTNIL []
        g (i:b) = Fun INSTLISTCONS [convInst i, g b]
        
convSep :: Sep -> MMTerm 
convSep x = case x of 
  SepVar y -> Var y
  SepSemi -> Fun SEPSEMI [] 
  SepComma -> Fun SEPCOMMA [] 
  SepBar -> Fun SEPBAR []

convLst :: Lst -> MMTerm 
convLst x = case x of
  LstVar y -> Var y
  Lst o sos -> Fun LST' [convObj o, g sos]
  where g [] = Fun SEPOBJLISTNIL []
        g ((p,q):b) = Fun SEPOBJLISTCONS [convSep p, convObj q, g b]
  
convRec :: Rec -> MMTerm 
convRec x = case x of
  RecVar y -> Var y
  Rec o o' soos -> Fun REC' [convObj o, convObj o', g soos]
  where g [] = Fun SEPOBJOBJLISTNIL []
        g ((p,q,l):b) = Fun SEPOBJOBJLISTCONS [convSep p, convObj q, convObj l, g b]
 
convInst :: Inst -> MMTerm 
convInst x = case x of
  InstVar y -> Var y
  Inst op is -> Fun INST' [convOp op, g is]
  where g [] = Fun OBJLISTNIL []
        g (a:b) = Fun OBJLISTCONS [convObj a, g b]

convPoly :: Poly -> MMTerm 
convPoly x = case x of
  PolyVar y -> Var y
  LstPoly y -> Fun LSTPOLY [convLst y]
  RecPoly y -> Fun RECPOLY [convRec y] 
  InstPoly y -> Fun INSTPOLY [convInst y]
  
convOp :: Op -> MMTerm 
convOp x = case x of
  OpVar y -> Var y 
  x -> Fun (OP' x) [] 
   
-------------------------------------------------------------------------------
-- Abstract Syntax (suitable as the target of e.g., PEG parsing)
-- Includes variables (todo: discuss with Marko about their use in general)
                                        
data Obj = TypeObj Type | ValueObj Value | ObjVar String
 deriving (Eq, Ord)
 
data Value = BoolValue Bool | IntValue Integer | StrValue String  | ValueVar String
 deriving (Eq, Ord)

data Type = CTypeType CType | DTypeType DType  | TypeVar String 
 deriving (Eq, Ord)
 
data CType = BoolType | PolyType | AnonType | IntType | StrType | CTypeVar String
 deriving (Eq, Ord)
   
data DType = DType CType (Maybe CType) [Inst] | DTypeVar String
 deriving (Eq, Ord) 
 
data Sep = SepSemi | SepComma | SepBar | SepVar String 
 deriving (Eq, Ord) 
  
data Lst = Lst Obj [(Sep, Obj)] | LstVar String 
 deriving (Eq, Ord) 
 
data Rec = Rec Obj Obj [(Sep, Obj, Obj)] | RecVar String 
 deriving (Eq, Ord) 

data Inst = Inst Op [Obj] | InstVar String 
 deriving (Eq, Ord) 
 
data Poly = LstPoly Lst | RecPoly Rec | InstPoly Inst | PolyVar String    
 deriving (Eq, Ord)

data Op = AOp | AddOp | AndOp | AsOp | CombineOp | CountOp | EqOp | ErrorOp | ExplainOp | 
  FoldOp | FromOp | GetOp | GivenOp | GrpCountOp | GtOp | GteOp | HeadOp | IdOp | IsOp | 
  LastOp | Lt | Lte | MapOp | MergeOp | MultOp | NegOp | NoOpOp | OneOp | OrOp | PathOp | 
  PlusOp | PowOp | PutOp | QOp | RepeatOp | SplitOp | StartOp | TailOp | ToOp | TraceOp | 
  TypeOp | ZeroOp | OpVar String 
 deriving (Eq, Ord)

-----------------------------------------------
-- Pretty Printing
-----------------------------------------------

names = [(AOp, "a")
 ,(AddOp, "plus")
 ,(AndOp, "and")
 ,(AsOp, "as")
 ,(CombineOp, "combine")
 ,(CountOp, "count")
 ,(EqOp, "=")
 ,(ErrorOp, "error")
 ,(ExplainOp, "explain")
 ,(FoldOp, "fold")
 ,(FromOp, "from")
 ,(GetOp, "get")
 ,(GivenOp, "given")
 ,(GrpCountOp, "groupcount")
 ,(GtOp, ">")
 ,(GteOp, ">=")
 ,(HeadOp, "head")
 ,(IdOp, "id")
 ,(IsOp, "is")
 ,(LastOp, "last")
 ,(Lt, "<")
 ,(Lte, "<=")
 ,(MapOp, "map")  
 ,(MergeOp, "merge")
 ,(MultOp, "mult")
 ,(NegOp, "-")
 ,(NoOpOp, "noop")
 ,(OneOp, "one")
 ,(OrOp, "or")
 ,(PathOp, "path")
 ,(PlusOp, "plus")
 ,(PowOp, "pow")
 ,(PutOp, "put")
 ,(QOp, "q")
 ,(RepeatOp, "repeat") 
 ,(SplitOp, "split")
 ,(StartOp, "start")
 ,(TailOp, "tail")
 ,(ToOp, "to")
 ,(TraceOp, "trace")
 ,(TypeOp, "type")
 ,(ZeroOp, "zero")]

namesRev = zip b a
 where (a, b) = unzip names
 
sep [] _ = ""
sep [a] _ = a
sep (a:b) s = a ++ s ++ (sep b s)
   
instance Show Obj where
  show x = case x of 
    TypeObj t -> show t 
    ValueObj v -> show v 
    ObjVar v -> v
    
instance Show Value where
  show x = case x of 
    BoolValue b -> show b 
    IntValue  i -> show i 
    StrValue  s -> show s 
    ValueVar  v -> v 
  
instance Show CType where
  show x = case x of
    BoolType  -> "bool"
    PolyType  -> "poly"
    AnonType  -> "_"
    IntType   -> "int"
    StrType   -> "str"

instance Show Type where
  show x = case x of
    CTypeType c -> show c
    DTypeType d -> show d
    TypeVar v -> v 
   
instance Show Sep where
  show x = case x of
    SepSemi -> ";"
    SepComma -> ","
    SepBar -> "|"
    SepVar v -> v 
    
instance Show Poly where
  show x = case x of  
    LstPoly l -> show l
    RecPoly r -> show r
    InstPoly i -> show i
    PolyVar v -> v 

instance Show Op where 
  show x = fromJust $ lookup x names 

instance Show Lst where 
  show (Lst o sos) = "(" ++ show o ++ sep (map f sos) " " ++ ")"
    where f (s, o') = show s ++ " " ++ show o'  

instance Show Inst where
  show (Inst o os) = "[" ++ show o ++ sep (map f os) " " ++ "]" 
    where f o' = ", " ++ show o'  
     
instance Show DType where
  show (DType c cs is) = show c ++ " " ++ oc ++ " " ++ sep (map show is) " <= " 
    where oc = case cs of 
                Just y -> show y 
                Nothing -> ""   

instance Show Rec where
  show (Rec o1 o2 soos) = "(" ++ show o1 ++ " -> " ++ show o2 ++ sep (map f soos) " " 
    where f (s,o,o') = show s ++ " " ++ show o ++ " -> " ++ show o' 
    
-------------------------------------------------------------------
-- PEG Parsing (order matters).  
-------------------------------------------------------------------
   
lexer = Token.makeTokenParser languageDef

identifier = Token.identifier lexer
reserved   = Token.reserved   lexer 
parens     = Token.parens     lexer 
intParser  = Token.integer    lexer 
semi       = Token.semi       lexer 
comma      = Token.comma     lexer 
whiteSpace = Token.whiteSpace lexer 
strParser  = Token.stringLiteral lexer

languageDef =
   emptyDef { Token.commentStart    = "/*"
            , Token.commentEnd      = "*/"
            , Token.commentLine     = "//"
            , Token.identStart      = letter
            , Token.identLetter     = alphaNum
            , Token.reservedNames   = map snd names ++ ["|","true","false","->"]
            }

opParser :: Parser Op
opParser = choice $ map (\x->do { reserved x; return $ fromJust $ lookup x namesRev}) $ map snd names  

objParser :: Parser Obj
objParser = do { x <- typeParser; return $ TypeObj x }
 <|> do { x <- valueParser; return $ ValueObj x }

boolParser :: Parser Bool
boolParser = do { x <- string "true"; return $ True } 
 <|> do { x <- string "false"; return $ False }

valueParser :: Parser Value
valueParser = do { x <- boolParser; return $ BoolValue x }
 <|> do { x <- intParser; return $ IntValue x } 
 <|> do { x <- strParser; return $ StrValue x } 

typeParser :: Parser Type
typeParser = do { x <- dtypeParser; return $ DTypeType x }
 <|> do { x <- ctypeParser; return $ CTypeType x }
 
sepParser :: Parser Sep
sepParser = do { x <- semi; return $ SepSemi }
 <|>  do { x <- comma; return $ SepComma }
 <|>  do { x <- string "|"; return $ SepBar }

recParser :: Parser Rec
recParser = do { _ <- string "(";
  o1 <- objParser;
  _ <- string "->";
  o2 <- objParser;
  soos <- many p;
  _ <- string ")";
  return $ Rec o1 o2 soos }
 where p = do { s <- sepParser; o <- objParser; _ <- string "->"; o' <- objParser; return (s,o,o') }

polyParser :: Parser Poly 
polyParser = do { x <- lstParser; return $ LstPoly x }
 <|>  do { x <- recParser;  return $ RecPoly  x }
 <|>  do { x <- instParser; return $ InstPoly x }

ctypeParser :: Parser CType
ctypeParser = do { x <- string "bool"; return BoolType }
 <|>  do { x <- string "poly"; return PolyType }
 <|>  do { x <- string "_"; return AnonType }
 <|>  do { x <- string "int"; return IntType }
 <|>  do { x <- string "str"; return StrType }
 
dtypeParser :: Parser DType
dtypeParser = do { c <- ctypeParser;
  oc <- optionMaybe p; 
  is <- many instParser;
  return $ DType c oc is }
 where p = do { _ <- string "<="; ctypeParser }
 
lstParser :: Parser Lst
lstParser = do { _ <- string "(";
  o <- objParser;
  sos <- many p;
  _ <- string ")";
  return $ Lst o sos }
 where p = do { s <- sepParser; o <- objParser; return (s,o) }

instParser :: Parser Inst
instParser = do { _ <- string "[";
  o <- opParser;
  sos <- many p;
  _ <- string "]";
  return $ Inst o sos }
 where p = do { _ <- comma ; o <- objParser; return o }

--------------------------------------------------------------------
-- multi-sorted equational logic (extra bits not in term-rewriting)

typeOf sig g (Var v) = case lookup v g of
  Nothing -> Left $ "Not found: " ++ v ++ " in " ++ show g
  Just x -> Right x
typeOf sig g (Fun f as) = do { ts <- mapM (typeOf sig g) as
                             ; if fst (sig f) == ts 
                               then Right $ snd (sig f) 
                               else Left $ "No match on " ++ show f ++ " args " ++ show as ++ ": " ++ show ts }
                               










