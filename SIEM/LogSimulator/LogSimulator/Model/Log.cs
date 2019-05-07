using LogSimulator.Model.Enum;
using System;

namespace LogSimulator.Model
{
    public class Log
    {
        public long Id { get; set; }

        public long EventId { get; set; }

        public DateTime TimeStamp { get; set; }

        public LogLevelType LogLevelType { get; set; }

        public string Message { get; set; }

        public override string ToString()
        {
            return string.Join("|", Id, EventId, TimeStamp, LogLevelType, Message);
        }
    }
}
