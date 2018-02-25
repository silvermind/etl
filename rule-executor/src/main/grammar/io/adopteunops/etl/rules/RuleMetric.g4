grammar RuleMetric;

parse
 : 'SELECT' select_clause from window (where)? (group_by)? (having)? destination
 ;


select_clause
 : aggfunction
 ;

aggfunction
 : function_name '(' target ')'
 ;

from
 : 'FROM' target
 ;

window
 : 'WINDOW' ( tumblingWindowExpression | hoppingWindowExpression | sessionWindowExpression)
 ;

tumblingWindowExpression
 : 'TUMBLING' '(' INT timeunit ')'
 ;

hoppingWindowExpression
 : 'HOPPING' '(' INT timeunit ',' INT timeunit ')'
 ;

sessionWindowExpression
 : 'SESSION' '(' INT timeunit ')'
 ;

where
 : 'WHERE' expr
 ;

group_by
 : 'GROUP BY' fieldname
 ;

having
 : 'HAVING' RESULT COMPARISON_OPERATION INT
 ;

destination
 : 'TO' ( toSystemOutExpression | toKafkaTopic | toElasticsearch )
 ;

toSystemOutExpression
 : 'SYSTEM_OUT'
 ;

toKafkaTopic
 : 'KAFKA' target
 ;

toElasticsearch
 : 'ELASTICSEARCH' target
 ;

RESULT
 : 'result'
 ;

expr
 : '(' expr ')'                                   # subExpr
 | <assoc=right> expr '^' expr                    # exponentExpr
 | '(' expr ')'                                   # subExpr
 | expr HIGH_PRIORITY_OPERATION expr              # highPriorityOperationExpr
 | expr LOW_PRIORITY_OPERATION expr               # lowPriorityOperationExpr
 | expr COMPARISON_OPERATION expr                 # comparisonExpr
 | expr AND_OPERATION expr                        # andCondition
 | expr OR_OPERATION expr                         # orCondition
 | NOT_OPERATION '(' expr ')'                     # notCondition
 | 'IF'           '(' expr ',' expr ',' expr ')'  # ifCondition
 | functionname '(' fieldvalue ')'                # oneArgCondition
 | fieldvalue (NOT_OPERATION)? functionname '(' expr (',' expr)* ')' # varArgCondition
 | fieldvalue COMPARISON_OPERATION INT timeunit   # timeCondition
 | atom                                           # atomExpr
 | fieldvalue                                     # fieldvalueExpr
 ;

functionname
 : target
 ;

timeunit
 : ('SECONDS'|'S'|'s')
 | ('MINUTES'|'M'|'m')
 | ('HOURS'|'H'|'h')
 | ('DAYS'|'D'|'d')
 ;

HIGH_PRIORITY_OPERATION
 : '*'
 | '/'
 ;

LOW_PRIORITY_OPERATION
 : '+'
 | '-'
 ;

COMPARISON_OPERATION
 : '<>'
 | '!='
 | '='
 | '=='
 | '>'
 | '<'
 | '>='
 | '<='
 ;

AND_OPERATION
 : 'AND'
 | '&&'
 ;

OR_OPERATION
 : 'OR'
 | '||'
 ;

NOT_OPERATION
 : 'NOT'
 | '!'
 ;

atom
 : INT                # intAtom
 | BOOLEAN            # booleanAtom
 | FLOAT              # floatAtom
 | STRING             # stringAtom
 | 'null'             # nullAtom
 ;

fieldvalue
 : FIELD_NAME
 ;

fieldname
 : FIELD_NAME
 ;

function_name
 : FIELD_NAME
 ;

target
 : FIELD_NAME
 ;

BOOLEAN
 : 'true'
 | 'false'
 ;

STRING
 : '"' (ESC|.)*? '"'
 ;

fragment ESC
 : '\\"'
 | '\\\\'
 ;

FIELD_NAME
 : [a-zA-Z][A-Za-z0-9_]*
 ;

INT
 : [-]?[0-9]+
 ;

FLOAT
 : [-]?[0-9]+ '.' [0-9]*  // 1. or 1.1
 |        '.' [0-9]+  // .1
 ;

SPACE
 : [ \t\r\n] -> skip
 ;