using System;

namespace LogSimulator.Helper
{
    public class NoValidStateException : Exception
    {
        public NoValidStateException(string message) : base(message)
        {
        }

        public NoValidStateException()
        {
        }
    }
}
