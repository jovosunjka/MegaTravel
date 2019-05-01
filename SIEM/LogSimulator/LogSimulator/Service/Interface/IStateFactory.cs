using LogSimulator.Model.Enum;
using LogSimulator.State;

namespace LogSimulator.Service.Interface
{
    public interface IStateFactory
    {
        IState GetState(StateType stateType);

        IState GetRandomState();
    }
}
