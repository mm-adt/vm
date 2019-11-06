#!/bin/bash
#
# Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
#
# This file is part of mm-ADT.
#
# mm-ADT is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# mm-ADT is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
#
# You can be released from the requirements of the license by purchasing
# a commercial license from RReduX,Inc. at [info@rredux.com].

set -e
set -u

USER_DIR=$(pwd)

cd "$(dirname "$0")"
DIR=$(pwd)

SCRIPT_NAME="$(basename "$0")"
while [ -h "${SCRIPT_NAME}" ]; do
  SOURCE="$(readlink "${SCRIPT_NAME}")"
  DIR="$( cd -P "$( dirname "${SOURCE}" )" && pwd )"
  cd "${DIR}"
done

cd ..

JAVA_OPTIONS=${JAVA_OPTIONS:-}

case $(uname) in
  CYGWIN*)
    CP="${CP:-}";$( echo lib/*.jar . | sed 's/ /;/g')
    ;;
  *)
    CP="${CP:-}":$( echo lib/*.jar . | sed 's/ /:/g')
esac

export CLASSPATH="${CLASSPATH:-}:$CP"

# Find Java
if [ -z "${JAVA_HOME:-}" ]; then
    JAVA="java -server"
else
    JAVA="$JAVA_HOME/bin/java -server"
fi

# JVM_OPTS=()
# if [ ! -z "${JAVA_OPTIONS}" ]; then
#    JVM_OPTS+=( "${JAVA_OPTIONS}" )
# fi

exec $JAVA org.mmadt.machine.console.Console "$@"