import {useState} from 'react';
import {
    Container,
    Title,
    TextInput,
    Button,
    Text,
    Stack,
} from '@mantine/core';
import {IconLink} from '@tabler/icons-react';

function Home() {
    const [originalUrl, setOriginalUrl] = useState('');
    const [shortUrl, setShortUrl] = useState('');
    const [loading, setLoading] = useState(false);

    const handleGenerate = async () => {
        if(originalUrl === '') {
            alert('Please enter a URL');
            return;
        }
        setLoading(true);
        try {
            const response = await fetch(
                `${process.env.REACT_APP_URL_SHORTENER_BACKEND_HOSTED_URL}/url?accessToken=${process.env.REACT_APP_URL_SHORTENER_BACKEND_ACCESS_TOKEN}`,
                {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({url: originalUrl}),
                });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Something went wrong');
            }

            const data = await response.json();
            setShortUrl(data.url);
        } catch (error) {
            console.error('Failed to generate short URL:', error.message);
            alert('Error: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container
            size="xs"
            style={{
                height: '100vh',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
            }}
        >
            <Stack align="center" spacing="md">
                <Title order={2} align="center" c="white">
                    <IconLink style={{marginBottom: 8}}/> TetraLink - URL Shortener
                </Title>

                <Text c="gray.4" size="lg">
                    Enter the original URL *
                </Text>

                <TextInput
                    placeholder="https://example.com/very/long/url"
                    value={originalUrl}
                    onChange={(e) => setOriginalUrl(e.currentTarget.value)}
                    styles={{
                        input: {
                            height: 50,
                            width: 350,
                            fontSize: 16,
                            padding: '0 12px',
                            margin: '15px',
                            outline: 'none',
                        },
                    }}
                    radius="3md"
                />

                <Button
                    onClick={handleGenerate}
                    loading={loading}
                    loaderPosition="center"
                    size="md"
                    style={{
                        height: 45,
                        width: 200,
                        backgroundColor: '#d3e2fd',
                        cursor: "pointer",
                        boxShadow: loading ? '0 0 12px rgba(255, 0, 0, 0.9)' : 'none',
                        transition: 'box-shadow 0.3s ease-in-out',
                    }}
                >
                    Generate Short URL
                </Button>

                {shortUrl && (
                    <div
                        style={{
                            marginTop: '40px',
                            fontSize: '20px',
                            color: '#00ffff',
                            textAlign: 'center',
                        }}
                    >
                        Shortened URL:{' '}
                        <a
                            href={shortUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={{
                                color: '#00ffff',
                                textDecoration: 'underline',
                                fontWeight: 'bold',
                            }}
                        >
                            {shortUrl}
                        </a>
                    </div>
                )}

            </Stack>
            <div
                style={{
                    position: 'fixed',
                    bottom: '20px',
                    width: '100%',
                    textAlign: 'center',
                }}
            >
                <a
                    href="https://github.com/yashasviyadav1/TetraLink"
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                        color: '#f2504f',
                        textDecoration: 'none',
                        fontSize: '18px',
                        marginBottom: '50px',
                    }}
                >
                    View on Github
                </a>
            </div>
        </Container>

    );
}

export default Home;
