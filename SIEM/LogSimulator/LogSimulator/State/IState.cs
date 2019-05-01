namespace LogSimulator.State
{
    public interface IState
    {
        string Description { get; }

        void Simulate(string logFilePath);
    }
}
