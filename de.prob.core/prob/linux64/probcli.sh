#!/bin/bash
PROBCOMMAND=probcli
INTERRUPT_COMMAND=send_user_interrupt


# Shell wrapper for PROBCOMMAND

echo "Running ProB Command-line Interface"
echo "$PROBCOMMAND" "$@"

# dirname
dirname=`dirname "$0"`

ulimit -d unlimited

chmod a+x "$dirname/$PROBCOMMAND"
chmod a+x "$dirname/$INTERRUPT_COMMAND"
exec "$dirname/$PROBCOMMAND" "$@"
