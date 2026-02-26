#include <iostream>
#include <vector>
#include <random>
#include <fstream>
#include <chrono>   
#include <thread>   
#include <iomanip>  
#include <numeric>

using namespace std;

// Точка з вагою
struct Point {
    double x;
    double y;
    double weight;
};

// Допоміжна структура для результатів обчислень (суми)
struct CalculationResult {
    double sumXW = 0.0; // Сума (x * вага)
    double sumYW = 0.0; // Сума (y * вага)
    double sumW = 0.0; // Сума ваг
};

vector<Point> generateData(size_t count) {
    vector<Point> points(count);
    random_device rd;
    mt19937 gen(rd());
    uniform_real_distribution<> coordDist(-1000.0, 1000.0); // Координати від -1000 до 1000
    uniform_real_distribution<> weightDist(1.0, 100.0);     // Вага від 1 до 100

    for (size_t i = 0; i < count; ++i) {
        points[i].x = coordDist(gen);
        points[i].y = coordDist(gen);
        points[i].weight = weightDist(gen);
    }
    return points;
}

// Збереження даних у файл
void saveToFile(const vector<Point>& points, const string& filename) {
    ofstream file(filename, ios::binary);
    if (!file.is_open()) {
        cerr << "Error writing file!" << endl;
        return;
    }
    size_t size = points.size();
    file.write((char*)&size, sizeof(size));
    file.write((char*)points.data(), size * sizeof(Point));
    file.close();
    cout << "Data saved to " << filename << endl;
}

// Зчитування даних із файлу
vector<Point> loadFromFile(const string& filename) {
    ifstream file(filename, ios::binary);
    if (!file.is_open()) {
        cerr << "Error reading file!" << endl;
        return {};
    }
    size_t size;
    file.read((char*)&size, sizeof(size));
    vector<Point> points(size);
    file.read((char*)points.data(), size * sizeof(Point));
    file.close();
    cout << "Data loaded from " << filename << endl;
    return points;
}

// Послідовний алгоритм
CalculationResult solveSequential(const vector<Point>& points) {
    CalculationResult res;
    for (const auto& p : points) {
        res.sumXW += p.x * p.weight;
        res.sumYW += p.y * p.weight;
        res.sumW += p.weight;
    }
    return res;
}

// Паралельний алгоритм 
void workerThread(const vector<Point>& points, size_t start, size_t end, CalculationResult& result) {
    double lx = 0, ly = 0, lw = 0;

    for (size_t i = start; i < end; ++i) {
        lx += points[i].x * points[i].weight;
        ly += points[i].y * points[i].weight;
        lw += points[i].weight;
    }

    result.sumXW = lx;
    result.sumYW = ly;
    result.sumW = lw;
}

// Головна функція паралельних обчислень
CalculationResult solveParallel(const vector<Point>& points, unsigned int numThreads) {
    vector<thread> threads;
    vector<CalculationResult> partialResults(numThreads); // Результат кожного потоку

    size_t totalPoints = points.size();
    size_t blockSize = totalPoints / numThreads;

    for (unsigned int i = 0; i < numThreads; ++i) {
        size_t start = i * blockSize;
        size_t end = (i == numThreads - 1) ? totalPoints : (i + 1) * blockSize;

        // Створення потоку. Передаємо функцію workerThread і аргументи
        threads.emplace_back(workerThread, cref(points), start, end, ref(partialResults[i]));
    }

    // Очікування завершення всіх потоків 
    for (auto& t : threads) {
        t.join();
    }

    // Зведення результатів 
    CalculationResult finalRes;
    for (const auto& part : partialResults) {
        finalRes.sumXW += part.sumXW;
        finalRes.sumYW += part.sumYW;
        finalRes.sumW += part.sumW;
    }
    return finalRes;
}

int main() {
    size_t N = 50000000;

    cout << "Generating " << N << " random points..." << endl;
    auto data = generateData(N);

    saveToFile(data, "data.bin");
    // data = loadFromFile("data.bin");

    cout << "Starting calculations...\n" << endl;

    // Послідовне виконання + заміри часу
    auto startSeq = chrono::high_resolution_clock::now();
    CalculationResult resSeq = solveSequential(data);
    auto endSeq = chrono::high_resolution_clock::now();

    chrono::duration<double> timeSeq = endSeq - startSeq;

    cout << "[Sequential Algorithm]" << endl;
    cout << "Time: " << timeSeq.count() << " sec" << endl;
    cout << "Center X: " << resSeq.sumXW / resSeq.sumW << endl;
    cout << "Center Y: " << resSeq.sumYW / resSeq.sumW << endl << endl;

    // Паралельне виконання + заміри часу
    unsigned int threadsCount = thread::hardware_concurrency();
    if (threadsCount == 0) threadsCount = 4;

    auto startPar = chrono::high_resolution_clock::now();
    CalculationResult resPar = solveParallel(data, threadsCount);
    auto endPar = chrono::high_resolution_clock::now();

    chrono::duration<double> timePar = endPar - startPar;

    cout << "[Parallel Algorithm (" << threadsCount << " threads)]" << endl;
    cout << "Time: " << timePar.count() << " sec" << endl;
    cout << "Center X: " << resPar.sumXW / resPar.sumW << endl;
    cout << "Center Y: " << resPar.sumYW / resPar.sumW << endl << endl;

    cout << "Speedup: " << timeSeq.count() / timePar.count() << "x times faster" << endl;

    system("pause");
    return 0;
}