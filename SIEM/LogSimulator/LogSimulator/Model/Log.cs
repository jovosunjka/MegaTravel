using LogSimulator.Model.Enum;
using System;

namespace LogSimulator.Model
{
    public class Log
    {
        public long Id { get; set; }

        public long EventId { get; set; }

        public DateTime Timestamp { get; set; }

        public LogLevelType LogLevel { get; set; }

        public LogCategory LogCategory { get; set; }

        public string Message { get; set; }

        public override string ToString()
        {
            return string.Join("|", nameof(Id).ToLower() + ":" + Id, 
                                    nameof(EventId).ToLower() + ":" + EventId, 
                                    nameof(Timestamp).ToLower() + ":" + Timestamp,
                                    nameof(LogLevel).ToLower() + ":" + LogLevel,
                                    nameof(LogCategory).ToLower() + ":" + LogCategory,
                                    nameof(Message).ToLower() + ":" + Message);
        }
    }
}
