using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    // Each class which implements this interface needs
    // to be named as corresponding enum value !!!
    public interface IState
    {
        string Description { get; }

        void Simulate(IAppSettings appSettings);
    }
}
