using System;
using System.Collections.Generic;

namespace LogSimulator.Service.Interface
{
    public interface IStateService
    {
        List<Type> StateTypes { get; }

        List<string> StateDescriptions { get; }
    }
}
